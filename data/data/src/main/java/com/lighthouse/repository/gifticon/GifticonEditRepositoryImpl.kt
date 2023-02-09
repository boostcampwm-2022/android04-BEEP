package com.lighthouse.repository.gifticon

import com.lighthouse.beep.model.gifticon.GifticonForAddition
import com.lighthouse.beep.model.gifticon.GifticonForUpdate
import com.lighthouse.domain.repository.gifticon.GifticonEditRepository
import javax.inject.Inject

internal class GifticonEditRepositoryImpl @Inject constructor(
    private val gifticonEditDatabaseRepository: GifticonEditDatabaseRepository,
    private val gifticonStorageRepository: GifticonStorageRepository
) : GifticonEditRepository {

    override suspend fun insertGifticons(
        userId: String,
        gifticonForAdditionList: List<GifticonForAddition>
    ): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun updateGifticon(gifticonForUpdate: GifticonForUpdate): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteGifticon(
        userId: String,
        gifticonId: String
    ): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun transferGifticon(
        oldUserId: String,
        newUserId: String
    ): Result<Unit> {
        TODO("Not yet implemented")
    }
}
