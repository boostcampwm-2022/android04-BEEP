package com.lighthouse.repository.gifticon

import android.net.Uri
import com.lighthouse.beep.model.gifticon.GifticonCropImageResult
import com.lighthouse.beep.model.gifticon.GifticonImageResult

interface GifticonStorageRepository {

    suspend fun saveImage(
        userId: String,
        inputOriginUri: Uri?,
        inputCropUri: Uri?
    ): Result<GifticonImageResult>

    suspend fun updateCropImage(
        userId: String,
        inputCropUri: Uri?
    ): Result<GifticonCropImageResult>

    suspend fun deleteFile(uri: Uri): Result<Unit>
}
