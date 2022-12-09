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
import com.lighthouse.domain.model.GifticonForAddition
import com.lighthouse.mapper.toDomain
import com.lighthouse.util.recognizer.BalanceRecognizer
import com.lighthouse.util.recognizer.BarcodeRecognizer
import com.lighthouse.util.recognizer.ExpiredRecognizer
import com.lighthouse.util.recognizer.GifticonRecognizer
import com.lighthouse.util.recognizer.TextRecognizer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.Date
import javax.inject.Inject

class GifticonImageRecognizeSource @Inject constructor(
    @ApplicationContext private val context: Context
) {

    suspend fun recognize(id: Long, path: String): GifticonForAddition? {
        val originBitmap = getBitmap(path) ?: return null
        val info = GifticonRecognizer().recognize(originBitmap) ?: return null
        val croppedBitmap = info.croppedImage
        var croppedPath = ""
        if (croppedBitmap != null) {
            val croppedFile = context.getFileStreamPath("$TEMP_CROPPED_PREFIX$id")
            saveBitmap(croppedBitmap, CompressFormat.JPEG, 100, croppedFile)
            croppedPath = croppedFile.path
        }

        return info.toDomain(path, croppedPath)
    }

    suspend fun recognizeGifticonName(path: String): String {
        val bitmap = getBitmap(path) ?: return ""
        val inputs = TextRecognizer().recognize(bitmap)
        return inputs.joinToString("")
    }

    suspend fun recognizeBrandName(path: String): String {
        val bitmap = getBitmap(path) ?: return ""
        val inputs = TextRecognizer().recognize(bitmap)
        return inputs.joinToString("")
    }

    suspend fun recognizeBarcode(path: String): String {
        val bitmap = getBitmap(path) ?: return ""
        val result = BarcodeRecognizer().recognize(bitmap)
        return result.barcode
    }

    suspend fun recognizeBalance(path: String): Int {
        val bitmap = getBitmap(path) ?: return 0
        val result = BalanceRecognizer().recognize(bitmap)
        return result.balance
    }

    suspend fun recognizeExpired(path: String): Date {
        val bitmap = getBitmap(path) ?: return Date(0)
        val result = ExpiredRecognizer().recognize(bitmap)
        return result.expired
    }

    private suspend fun getBitmap(path: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            val originUri = Uri.parse(path)
            when (originUri.scheme) {
                SCHEME_CONTENT -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, originUri))
                } else {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, originUri)
                }
                SCHEME_FILE -> BitmapFactory.decodeFile(originUri.path)
                else -> null
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
        private const val TEMP_CROPPED_PREFIX = "temp_gifticon_"
    }
}
