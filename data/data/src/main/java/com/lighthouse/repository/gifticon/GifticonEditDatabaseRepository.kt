package com.lighthouse.repository.gifticon

import com.lighthouse.beep.model.gifticon.GifticonWithCrop
import com.lighthouse.beep.model.result.DbResult

interface GifticonEditDatabaseRepository {
    suspend fun insertGifticons(
        gifticonWithCropList: List<GifticonWithCrop>
    ): DbResult<Unit>

    suspend fun updateGifticon(
        gifticonWithCrop: GifticonWithCrop
    ): DbResult<Unit>

    suspend fun deleteGifticon(
        userId: String,
        gifticonId: String
    ): DbResult<Unit>

    suspend fun moveUserIdGifticon(
        oldUserId: String,
        newUserId: String
    ): DbResult<Unit>
}
