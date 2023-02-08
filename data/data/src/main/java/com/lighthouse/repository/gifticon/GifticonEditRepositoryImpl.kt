package com.lighthouse.repository.gifticon

import com.lighthouse.beep.model.gifticon.GifticonWithCrop
import com.lighthouse.domain.repository.GifticonEditRepository
import javax.inject.Inject

internal class GifticonEditRepositoryImpl @Inject constructor(
    private val gifticonEditDatabaseRepository: GifticonEditDatabaseRepository
) : GifticonEditRepository {

    override suspend fun insertGifticons(gifticonWithCropList: List<GifticonWithCrop>): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun updateGifticon(gifticonWithCrop: GifticonWithCrop): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteGifticon(
        userId: String,
        gifticonId: String
    ): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun moveUserIdGifticon(
        oldUserId: String,
        newUserId: String
    ): Result<Unit> {
        TODO("Not yet implemented")
    }
}
