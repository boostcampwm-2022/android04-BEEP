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
import com.lighthouse.util.recognizer.GifticonRecognizer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class GifticonImageRecognizeSource @Inject constructor(
    @ApplicationContext private val context: Context
) {

    suspend fun recognize(id: Long, path: String): GifticonForAddition? {
        val originUri = Uri.parse(path)
        val originBitmap = when (originUri.scheme) {
            SCHEME_CONTENT -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, originUri))
            } else {
                MediaStore.Images.Media.getBitmap(context.contentResolver, originUri)
            }
            SCHEME_FILE -> BitmapFactory.decodeFile(originUri.path)
            else -> return null
        }
        val info = GifticonRecognizer().recognize(originBitmap) ?: return null
        val croppedBitmap = info.croppedImage
        var croppedPath = ""
        if (croppedBitmap != null) {
            val croppedFile = context.getFileStreamPath("$TEMP_CROPPED_PREFIX$id")
            saveBitmap(croppedBitmap, CompressFormat.JPEG, 100, croppedFile)
            croppedPath = croppedFile.path
        }

        return GifticonForAddition(
            true,
            info.name,
            info.brand,
            info.barcode,
            info.expiredAt,
            info.isCashCard,
            info.balance,
            path,
            croppedPath,
            info.croppedRect.toDomain(),
            ""
        )
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
