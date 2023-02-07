package com.lighthouse.datasource.gifticon

import android.content.Context
import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import androidx.core.net.toUri
import com.lighthouse.core.android.exts.calculateSampleSize
import com.lighthouse.core.android.exts.centerCrop
import com.lighthouse.core.android.exts.compressBitmap
import com.lighthouse.core.android.exts.decodeBitmap
import com.lighthouse.core.android.exts.decodeSampledBitmap
import com.lighthouse.core.android.exts.deleteFile
import com.lighthouse.core.android.exts.exists
import com.lighthouse.core.android.exts.openInputStream
import com.lighthouse.model.GifticonImageResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileOutputStream
import java.util.Date
import javax.inject.Inject

class GifticonImageSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun saveImage(id: String, originUri: Uri?, oldCroppedUri: Uri?): GifticonImageResult =
        withContext(Dispatchers.IO) {
            originUri ?: throw NullPointerException("originUri 가 Null 입니다.")
            oldCroppedUri ?: throw NullPointerException("oldCroppedUri 가 Null 입니다.")

            val outputOriginFile = context.getFileStreamPath("$ORIGIN_PREFIX$id")

            val sampleSize = context.calculateSampleSize(originUri)
            val sampledOriginBitmap = context.decodeSampledBitmap(originUri, sampleSize)
            outputOriginFile.compressBitmap(sampledOriginBitmap, CompressFormat.JPEG, 100)

            val updated = Date()
            val outputCroppedFile = context.getFileStreamPath("${CROPPED_PREFIX}$id${updated.time}")
            val cropped = if (context.exists(oldCroppedUri)) {
                context.decodeBitmap(oldCroppedUri).also {
                    context.deleteFile(oldCroppedUri)
                }
            } else {
                sampledOriginBitmap.centerCrop(1f)
            }
            outputCroppedFile.compressBitmap(cropped, CompressFormat.JPEG, 100)
            GifticonImageResult(sampleSize, outputCroppedFile.toUri())
        }

    suspend fun updateImage(id: String, oldCroppedUri: Uri?, newCroppedUri: Uri?): Uri =
        withContext(Dispatchers.IO) {
            oldCroppedUri ?: throw NullPointerException("oldCroppedUri 가 Null 입니다.")
            newCroppedUri ?: throw NullPointerException("newCroppedUri 가 Null 입니다.")
            context.deleteFile(oldCroppedUri)

            val updated = Date()
            val outputCropped = context.getFileStreamPath("$CROPPED_PREFIX$id${updated.time}")
            context.openInputStream(newCroppedUri).copyTo(FileOutputStream(outputCropped))
            outputCropped.toUri()
        }

    companion object {
        private const val ORIGIN_PREFIX = "origin"
        private const val CROPPED_PREFIX = "cropped"
    }
}
