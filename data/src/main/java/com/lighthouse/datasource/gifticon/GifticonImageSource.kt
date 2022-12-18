package com.lighthouse.datasource.gifticon

import android.content.ContentResolver.SCHEME_CONTENT
import android.content.ContentResolver.SCHEME_FILE
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.net.toFile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.Date
import javax.inject.Inject

class GifticonImageSource @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val screenWidth = context.resources.displayMetrics.widthPixels
    private val screenHeight = context.resources.displayMetrics.heightPixels

    suspend fun saveImage(id: String, originUri: Uri?, croppedUri: Uri?) {
        originUri ?: return
        croppedUri ?: return

        val outputOriginFile = context.getFileStreamPath("$ORIGIN_PREFIX$id")

        val inputOriginStream = openInputStream(originUri) ?: return
        val sampleSize = calculateSampleSize(inputOriginStream)

        val originBitmap = decodeBitmap(originUri) ?: return
        val sampledOriginBitmap = samplingBitmap(originBitmap, sampleSize)
        saveBitmap(sampledOriginBitmap, CompressFormat.JPEG, 100, outputOriginFile)

        val updated = Date()
        val outputCroppedFile = context.getFileStreamPath("$CROPPED_PREFIX$id${updated.time}.jpg")
        val cropped = if (exists(croppedUri)) {
            decodeBitmap(croppedUri).also { deleteIfFile(croppedUri) } ?: return
        } else {
            centerCropBitmap(sampledOriginBitmap, 1f)
        }
        saveBitmap(cropped, CompressFormat.JPEG, QUALITY, outputCroppedFile)
    }

    suspend fun updateImage(id: String, oldCroppedUri: Uri?, newCroppedUri: Uri?) {
        oldCroppedUri ?: return
        newCroppedUri ?: return
        deleteIfFile(oldCroppedUri)

        val updated = Date()
        val outputCropped = context.getFileStreamPath("$CROPPED_PREFIX$id${updated.time}.jpg")
        withContext(Dispatchers.IO) {
            openInputStream(newCroppedUri)?.use { input ->
                FileOutputStream(outputCropped).use { output ->
                    input.copyTo(output)
                }
            } ?: return@withContext
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

    private suspend fun openInputStream(uri: Uri): InputStream? {
        return withContext(Dispatchers.IO) {
            when (uri.scheme) {
                SCHEME_CONTENT -> context.contentResolver.openInputStream(uri)
                SCHEME_FILE -> FileInputStream(uri.path)
                else -> null
            }
        }
    }

    private suspend fun decodeBitmap(uri: Uri): Bitmap? {
        return withContext(Dispatchers.IO) {
            when (uri.scheme) {
                SCHEME_CONTENT -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                        decoder.isMutableRequired = true
                    }
                } else {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
                SCHEME_FILE -> BitmapFactory.decodeFile(uri.path)
                else -> null
            }
        }
    }

    private fun deleteIfFile(uri: Uri) {
        if (uri.scheme == SCHEME_FILE) {
            uri.toFile().delete()
        }
    }

    private fun exists(uri: Uri): Boolean {
        return when (uri.scheme) {
            SCHEME_CONTENT -> {
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    cursor.moveToFirst()
                } ?: false
            }
            SCHEME_FILE -> uri.toFile().exists()
            else -> false
        }
    }

    private suspend fun samplingBitmap(bitmap: Bitmap, sampleSize: Int): Bitmap {
        return withContext(Dispatchers.IO) {
            Bitmap.createScaledBitmap(bitmap, bitmap.width / sampleSize, bitmap.height / sampleSize, false)
        }
    }

    private suspend fun centerCropBitmap(bitmap: Bitmap, aspectRatio: Float): Bitmap {
        return withContext(Dispatchers.IO) {
            val bitmapAspectRatio = bitmap.width.toFloat() / bitmap.height
            if (bitmapAspectRatio > aspectRatio) {
                val newWidth = (bitmap.height * aspectRatio).toInt()
                Bitmap.createBitmap(bitmap, (bitmap.width - newWidth) / 2, 0, newWidth, bitmap.height)
            } else {
                val newHeight = (bitmap.width / aspectRatio).toInt()
                Bitmap.createBitmap(bitmap, 0, (bitmap.height - newHeight) / 2, bitmap.width, newHeight)
            }
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
