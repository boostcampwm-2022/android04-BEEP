package com.lighthouse.repository.gifticon

import com.lighthouse.beep.model.gifticon.GifticonWithCrop

interface GifticonEditDatabaseRepository {

    suspend fun insertGifticons(
        gifticonWithCropList: List<GifticonWithCrop>
    ): Result<Unit>

    suspend fun updateGifticon(
        gifticonWithCrop: GifticonWithCrop
    ): Result<Unit>

    suspend fun deleteGifticon(
        userId: String,
        gifticonId: String
    ): Result<Unit>

    suspend fun transferGifticon(
        oldUserId: String,
        newUserId: String
    ): Result<Unit>
}
