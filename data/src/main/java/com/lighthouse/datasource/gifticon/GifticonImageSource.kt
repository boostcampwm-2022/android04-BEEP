package com.lighthouse.datasource.gifticon

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.lighthouse.domain.model.GifticonForAddition
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject
import kotlin.math.min

class GifticonImageSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val screenWidth = context.resources.displayMetrics.widthPixels

    private val screenHeight = context.resources.displayMetrics.heightPixels

    suspend fun saveImage(id: String, gifticon: GifticonForAddition) {
        if (gifticon.hasImage.not()) {
            return
        }

        val originUri = Uri.parse(gifticon.originUri) ?: return
        val outputOrigin = context.getFileStreamPath("$ORIGIN_PREFIX$id")

        val inputOrigin = context.contentResolver.openInputStream(originUri) ?: return
        val sampleSize = calculateSampleSize(inputOrigin)

        val sampledOrigin = decodeSampledBitmap(originUri, sampleSize) ?: return
        saveBitmap(sampledOrigin, CompressFormat.JPEG, 100, outputOrigin)
        sampledOrigin.recycle()

        val inputCropped = File(gifticon.croppedUri)
        val outputCropped = context.getFileStreamPath("$CROPPED_PREFIX$id")
        val cropped = if (inputCropped.exists()) {
            decodeBitmap(inputCropped).also {
                inputCropped.delete()
            }
        } else {
            val originBitmap = BitmapFactory.decodeFile(outputOrigin.path)
            val minSize = min(originBitmap.width, originBitmap.height)
            Bitmap.createBitmap(
                originBitmap,
                (originBitmap.width - minSize) / 2,
                (originBitmap.height - minSize) / 2,
                minSize,
                minSize
            )
        }
        saveBitmap(cropped, CompressFormat.JPEG, QUALITY, outputCropped)
    }

    suspend fun updateImage(id: String, croppedUri: String) {
        val outputCropped = context.getFileStreamPath("$CROPPED_PREFIX$id")
        withContext(Dispatchers.IO) {
            FileInputStream(croppedUri).use { input ->
                FileOutputStream(outputCropped).use { output ->
                    input.copyTo(output)
                }
            }
        }
    }

    private fun calculateSampleSize(inputStream: InputStream): Int {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds
        }
        BitmapFactory.decodeStream(inputStream, null, options)
        val imageWidth = options.outWidth
        val imageHeight = options.outHeight
        var inSampleSize = 1

        while (imageHeight / inSampleSize > screenHeight || imageWidth / inSampleSize > screenWidth) {
            inSampleSize *= 2
        }
        return inSampleSize
    }

    private suspend fun decodeSampledBitmap(originUri: Uri, sampleSize: Int): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, originUri))
                } else {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, originUri)
                }
                Bitmap.createScaledBitmap(bitmap, bitmap.width / sampleSize, bitmap.height / sampleSize, false)
            } catch (e: IOException) {
                null
            }
        }
    }

    private suspend fun decodeBitmap(file: File): Bitmap {
        return withContext(Dispatchers.IO) {
            BitmapFactory.decodeFile(file.path)
        }
    }

    private suspend fun saveBitmap(bitmap: Bitmap, format: CompressFormat, quality: Int, file: File) {
        withContext(Dispatchers.IO) {
            FileOutputStream(file).use { output ->
                bitmap.compress(format, quality, output)
            }
        }
    }

    companion object {
        private const val ORIGIN_PREFIX = "origin"
        private const val CROPPED_PREFIX = "cropped"

        private const val QUALITY = 70
    }
}
