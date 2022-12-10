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
import androidx.core.net.toUri
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

    suspend fun recognize(id: Long, uri: Uri?): GifticonForAddition? {
        uri ?: return null
        val originBitmap = decodeBitmap(uri) ?: return null
        val info = GifticonRecognizer().recognize(originBitmap)
        val croppedBitmap = info.croppedImage
        var croppedUri: Uri? = null
        if (croppedBitmap != null) {
            val croppedFile = context.getFileStreamPath("$TEMP_CROPPED_PREFIX$id")
            saveBitmap(croppedBitmap, CompressFormat.JPEG, 100, croppedFile)
            croppedUri = croppedFile.toUri()
        }
        return info.toDomain(uri, croppedUri)
    }

    suspend fun recognizeGifticonName(uri: Uri?): String {
        uri ?: return ""
        val bitmap = decodeBitmap(uri) ?: return ""
        val inputs = TextRecognizer().recognize(bitmap)
        return inputs.joinToString("")
    }

    suspend fun recognizeBrandName(uri: Uri?): String {
        uri ?: return ""
        val bitmap = decodeBitmap(uri) ?: return ""
        val inputs = TextRecognizer().recognize(bitmap)
        return inputs.joinToString("")
    }

    suspend fun recognizeBarcode(uri: Uri?): String {
        uri ?: return ""
        val bitmap = decodeBitmap(uri) ?: return ""
        return BarcodeRecognizer().recognize(bitmap).barcode
    }

    suspend fun recognizeBalance(uri: Uri?): Int {
        uri ?: return 0
        val bitmap = decodeBitmap(uri) ?: return 0
        val result = BalanceRecognizer().recognize(bitmap)
        return result.balance
    }

    suspend fun recognizeExpired(uri: Uri?): Date {
        uri ?: return Date(0)
        val bitmap = decodeBitmap(uri) ?: return Date(0)
        val result = ExpiredRecognizer().recognize(bitmap)
        return result.expired
    }

    private suspend fun decodeBitmap(uri: Uri): Bitmap? {
        return withContext(Dispatchers.IO) {
            when (uri.scheme) {
                SCHEME_CONTENT -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
                } else {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
                SCHEME_FILE -> BitmapFactory.decodeFile(uri.path)
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
