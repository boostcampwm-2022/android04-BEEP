package com.lighthouse.domain.repository.gifticon

import com.lighthouse.beep.model.gifticon.GifticonForAddition
import com.lighthouse.beep.model.gifticon.GifticonForUpdate

interface GifticonEditRepository {

    suspend fun insertGifticons(
        userId: String,
        gifticonForAdditionList: List<GifticonForAddition>
    ): Result<Unit>

    suspend fun updateGifticon(
        gifticonForUpdate: GifticonForUpdate
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
