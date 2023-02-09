package com.lighthouse.repository.gifticon

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.lighthouse.core.android.exts.calculateSampleSize
import com.lighthouse.core.android.exts.centerCrop
import com.lighthouse.core.android.exts.compressBitmap
import com.lighthouse.core.android.exts.decodeBitmap
import com.lighthouse.core.android.exts.decodeSampledBitmap
import com.lighthouse.core.android.exts.deleteFile
import com.lighthouse.core.android.exts.exists
import com.lighthouse.core.android.exts.openInputStream
import com.lighthouse.model.gifticon.GifticonCropImageResult
import com.lighthouse.model.gifticon.GifticonImageResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.Date
import javax.inject.Inject

internal class GifticonStorageRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    suspend fun saveImage(
        userId: String,
        inputOriginUri: Uri?,
        inputCropUri: Uri?
    ): Result<GifticonImageResult> = runCatching {
        withContext(Dispatchers.IO) {
            inputOriginUri ?: throw NullPointerException("originUri 가 Null 입니다.")
            inputCropUri ?: throw NullPointerException("oldCroppedUri 가 Null 입니다.")

            val outputOriginFile = getOriginFile(userId)

            val sampleSize = context.calculateSampleSize(inputOriginUri)
            val sampledOriginBitmap = context.decodeSampledBitmap(inputOriginUri, sampleSize)
            outputOriginFile.compressBitmap(sampledOriginBitmap, Bitmap.CompressFormat.JPEG, 100)

            val outputCroppedFile = getCropFile(userId)
            val cropped = if (context.exists(inputCropUri)) {
                context.decodeBitmap(inputCropUri)
            } else {
                sampledOriginBitmap.centerCrop(1f)
            }
            outputCroppedFile.compressBitmap(cropped, Bitmap.CompressFormat.JPEG, 100)
            GifticonImageResult(sampleSize, outputOriginFile, outputCroppedFile)
        }
    }

    suspend fun updateCropImage(
        userId: String,
        inputCropUri: Uri?
    ): Result<GifticonCropImageResult> = runCatching {
        withContext(Dispatchers.IO) {
            inputCropUri ?: throw NullPointerException("newCroppedUri 가 Null 입니다.")

            val outputCroppedFile = getCropFile(userId)

            val inputStream = context.openInputStream(inputCropUri)
            val outputStream = FileOutputStream(outputCroppedFile)

            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            GifticonCropImageResult(outputCroppedFile)
        }
    }

    suspend fun deleteFile(uri: Uri?): Result<Unit> = runCatching {
        withContext(Dispatchers.IO) {
            context.deleteFile(uri)
        }
    }

    private fun getOriginFile(userId: String): File {
        return context.getFileStreamPath("$ORIGIN_PREFIX$userId")
    }

    private fun getCropFile(userId: String, updated: Date = Date()): File {
        return context.getFileStreamPath("$CROPPED_PREFIX$userId${updated.time}")
    }

    companion object {
        private const val ORIGIN_PREFIX = "origin"
        private const val CROPPED_PREFIX = "cropped"
    }
}
