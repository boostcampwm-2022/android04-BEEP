package com.lighthouse.data.database.repository.gifticon

import com.lighthouse.beep.model.gifticon.GifticonWithCrop
import com.lighthouse.data.database.dao.GifticonEditDao
import com.lighthouse.data.database.ext.runCatchingDB
import com.lighthouse.data.database.mapper.gifticon.edit.toEntity
import com.lighthouse.repository.gifticon.GifticonEditDatabaseRepository
import javax.inject.Inject

internal class GifticonEditDatabaseRepositoryImpl @Inject constructor(
    private val gifticonEditDao: GifticonEditDao
) : GifticonEditDatabaseRepository {

    override suspend fun insertGifticons(
        gifticonWithCropList: List<GifticonWithCrop>
    ): Result<Unit> {
        return runCatchingDB {
            gifticonEditDao.insertGifticonWithCrop(gifticonWithCropList.toEntity())
        }
    }

    override suspend fun updateGifticon(
        gifticonWithCrop: GifticonWithCrop
    ): Result<Unit> {
        return runCatchingDB {
            gifticonEditDao.updateGifticonWithCrop(gifticonWithCrop.toEntity())
        }
    }

    override suspend fun deleteGifticon(
        userId: String,
        gifticonId: String
    ): Result<Unit> {
        return runCatchingDB {
            gifticonEditDao.deleteGifticon(userId, gifticonId)
        }
    }

    override suspend fun moveUserIdGifticon(
        oldUserId: String,
        newUserId: String
    ): Result<Unit> {
        return runCatchingDB {
            gifticonEditDao.moveUserIdGifticon(oldUserId, newUserId)
        }
    }
}
