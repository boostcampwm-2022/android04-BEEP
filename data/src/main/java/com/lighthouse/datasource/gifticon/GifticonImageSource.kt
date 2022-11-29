package com.lighthouse.datasource.gifticon

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.net.Uri
import com.lighthouse.domain.model.GifticonForAddition
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
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
        val inputOrigin = context.contentResolver.openInputStream(originUri) ?: return

        val outputOrigin = context.getFileStreamPath("$ORIGIN_PREFIX$id")
        val sampleSize = calculateSampleSize(inputOrigin)
        val sampledOrigin = decodeSampledBitmap(inputOrigin, sampleSize) ?: return
        saveBitmap(sampledOrigin, CompressFormat.JPEG, 70, outputOrigin)
        sampledOrigin.recycle()

        val inputCropped = File(gifticon.croppedUri)
        val outputCropped = context.getFileStreamPath("$CROPPED_PREFIX$id")
        if (inputCropped.exists()) {
            val sampledCropped = decodeSampledBitmap(inputCropped, sampleSize)
            saveBitmap(sampledCropped, CompressFormat.JPEG, 70, outputCropped)
            sampledCropped.recycle()
        } else {
            val originBitmap = BitmapFactory.decodeFile(outputOrigin.path)
            val minSize = min(originBitmap.width, originBitmap.height)
            val cropped = Bitmap.createBitmap(
                originBitmap,
                (originBitmap.width - minSize) / 2,
                (originBitmap.height - minSize) / 2,
                minSize,
                minSize
            )
            originBitmap.recycle()
            saveBitmap(cropped, CompressFormat.JPEG, 100, outputCropped)
            cropped.recycle()
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

    private suspend fun decodeSampledBitmap(inputStream: InputStream, sampleSize: Int): Bitmap? {
        return withContext(Dispatchers.IO) {
            BitmapFactory.decodeStream(
                inputStream,
                null,
                BitmapFactory.Options().apply {
                    inSampleSize = sampleSize
                }
            )
        }
    }

    private suspend fun decodeSampledBitmap(file: File, sampleSize: Int): Bitmap {
        return withContext(Dispatchers.IO) {
            BitmapFactory.decodeFile(
                file.path,
                BitmapFactory.Options().apply {
                    inSampleSize = sampleSize
                }
            )
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
    }
}
