package com.lighthouse.domain.repository

import com.lighthouse.beep.model.gifticon.GifticonWithCrop

interface GifticonEditRepository {

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
