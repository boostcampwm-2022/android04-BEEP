package com.lighthouse.repository.gifticon

import androidx.core.net.toUri
import com.lighthouse.beep.model.gifticon.GifticonForAddition
import com.lighthouse.beep.model.gifticon.GifticonForUpdate
import com.lighthouse.common.mapper.toUri
import com.lighthouse.core.utils.uuid.UUID
import com.lighthouse.domain.repository.gifticon.GifticonEditRepository
import com.lighthouse.mapper.toGifticonWithCrop
import com.lighthouse.model.gifticon.GifticonCropImageResult
import com.lighthouse.model.gifticon.GifticonImageResult
import javax.inject.Inject

internal class GifticonEditRepositoryImpl @Inject constructor(
    private val gifticonEditDatabaseRepository: GifticonEditDatabaseRepository,
    private val gifticonStorageRepository: GifticonStorageRepository
) : GifticonEditRepository {

    override suspend fun insertGifticons(
        userId: String,
        gifticonForAdditionList: List<GifticonForAddition>
    ): Result<Unit> = runCatching {
        val newGifticons = gifticonForAdditionList.map { gifticonForAddition ->
            val gifticonId = UUID.generate()
            var result: GifticonImageResult? = null
            if (gifticonForAddition.hasImage) {
                val tempCropUri = gifticonForAddition.tempCroppedUri.toUri()
                result = gifticonStorageRepository.saveImage(
                    gifticonId,
                    gifticonForAddition.originUri.toUri(),
                    tempCropUri
                ).getOrThrow()
                gifticonStorageRepository.deleteFile(tempCropUri).getOrThrow()
            }
            gifticonForAddition.toGifticonWithCrop(userId, gifticonId, result)
        }
        gifticonEditDatabaseRepository.insertGifticons(newGifticons).getOrThrow()
    }

    override suspend fun updateGifticon(
        gifticonForUpdate: GifticonForUpdate
    ): Result<Unit> = runCatching {
        var result: GifticonCropImageResult? = null
        if (gifticonForUpdate.isUpdatedImage) {
            result = gifticonStorageRepository.updateCropImage(
                gifticonForUpdate.id,
                gifticonForUpdate.croppedUri.toUri()
            ).getOrThrow()
            gifticonStorageRepository.deleteFile(
                gifticonForUpdate.oldCroppedUri.toUri()
            ).getOrThrow()
        }
        gifticonEditDatabaseRepository.updateGifticon(
            gifticonForUpdate.toGifticonWithCrop(result?.croppedFile?.toUri())
        ).getOrThrow()
    }

    override suspend fun deleteGifticon(
        userId: String,
        gifticonId: String
    ): Result<Unit> {
        return gifticonEditDatabaseRepository.deleteGifticon(userId, gifticonId)
    }

    override suspend fun transferGifticon(
        oldUserId: String,
        newUserId: String
    ): Result<Unit> {
        return gifticonEditDatabaseRepository.transferGifticon(oldUserId, newUserId)
    }
}
