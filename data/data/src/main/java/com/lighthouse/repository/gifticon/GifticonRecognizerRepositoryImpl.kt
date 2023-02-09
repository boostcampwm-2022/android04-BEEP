package com.lighthouse.repository.gifticon

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import com.lighthouse.beep.model.gallery.GalleryImage
import com.lighthouse.beep.model.gifticon.GifticonRecognizeResult
import com.lighthouse.core.android.exts.compressBitmap
import com.lighthouse.core.android.exts.decodeBitmap
import com.lighthouse.domain.repository.gifticon.GifticonRecognizeRepository
import com.lighthouse.mapper.toDomain
import com.lighthouse.utils.recognizer.BalanceRecognizer
import com.lighthouse.utils.recognizer.BarcodeRecognizer
import com.lighthouse.utils.recognizer.ExpiredRecognizer
import com.lighthouse.utils.recognizer.GifticonRecognizer
import com.lighthouse.utils.recognizer.TextRecognizer
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.util.Date
import javax.inject.Inject

internal class GifticonRecognizerRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : GifticonRecognizeRepository {

    override suspend fun recognize(
        gallery: GalleryImage
    ): Result<GifticonRecognizeResult> = runCatching {
        val uri = gallery.contentUri.toUri()
        val originBitmap = context.decodeBitmap(uri)
        val info = GifticonRecognizer().recognize(originBitmap)
        val croppedBitmap = info.croppedImage
        var croppedUri: Uri? = null
        if (croppedBitmap != null) {
            val croppedFile = getTempCropFile(gallery.id)
            croppedFile.compressBitmap(croppedBitmap, Bitmap.CompressFormat.JPEG, 100)
            croppedUri = croppedFile.toUri()
        }
        info.toDomain(uri, croppedUri)
    }

    override suspend fun recognizeText(path: String): Result<String> = runCatching {
        val uri = path.toUri()
        val bitmap = context.decodeBitmap(uri)
        val inputs = TextRecognizer().recognize(bitmap)
        inputs.joinToString("")
    }

    override suspend fun recognizeBarcode(path: String): Result<String> = runCatching {
        val uri = path.toUri()
        val bitmap = context.decodeBitmap(uri)
        BarcodeRecognizer().recognize(bitmap).barcode
    }

    override suspend fun recognizeBalance(path: String): Result<Int> = runCatching {
        val uri = path.toUri()
        val bitmap = context.decodeBitmap(uri)
        BalanceRecognizer().recognize(bitmap).balance
    }

    override suspend fun recognizeExpired(path: String): Result<Date> = runCatching {
        val uri = path.toUri()
        val bitmap = context.decodeBitmap(uri)
        ExpiredRecognizer().recognize(bitmap).expired
    }

    private fun getTempCropFile(contentId: Long): File {
        return context.getFileStreamPath("$TEMP_CROPPED_PREFIX$contentId")
    }

    companion object {
        private const val TEMP_CROPPED_PREFIX = "temp_gifticon_"
    }
}
