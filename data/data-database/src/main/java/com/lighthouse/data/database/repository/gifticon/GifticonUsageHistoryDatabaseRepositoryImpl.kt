package com.lighthouse.data.database.repository.gifticon

import com.lighthouse.beep.model.result.DbResult
import com.lighthouse.beep.model.user.UsageHistory
import com.lighthouse.data.database.dao.GifticonUsageHistoryDao
import com.lighthouse.data.database.ext.runCatchingDB
import com.lighthouse.data.database.mapper.gifticon.usage.toDomain
import com.lighthouse.data.database.mapper.gifticon.usage.toEntity
import com.lighthouse.repository.gifticon.GifticonUsageHistoryDatabaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class GifticonUsageHistoryDatabaseRepositoryImpl @Inject constructor(
    private val gifticonUsageHistoryDao: GifticonUsageHistoryDao
) : GifticonUsageHistoryDatabaseRepository {

    override suspend fun useGifticon(
        userId: String,
        gifticonId: String,
        usageHistory: UsageHistory
    ): DbResult<Unit> {
        return runCatchingDB {
            gifticonUsageHistoryDao.useGifticonAndInsertHistory(
                userId,
                usageHistory.toEntity(gifticonId)
            )
        }
    }

    override suspend fun useCashCardGifticon(
        userId: String,
        gifticonId: String,
        amount: Int,
        usageHistory: UsageHistory
    ): DbResult<Unit> {
        return runCatchingDB {
            gifticonUsageHistoryDao.useCashCardGifticonAndInsertHistory(
                userId,
                amount,
                usageHistory.toEntity(gifticonId)
            )
        }
    }

    override suspend fun revertUsedGifticon(
        userId: String,
        gifticonId: String
    ): DbResult<Unit> {
        return runCatchingDB {
            gifticonUsageHistoryDao.revertUsedGifticonAndDeleteHistory(
                userId,
                gifticonId
            )
        }
    }

    override fun getUsageHistory(
        userId: String,
        gifticonId: String
    ): DbResult<Flow<List<UsageHistory>>> {
        return runCatchingDB {
            gifticonUsageHistoryDao.getUsageHistory(
                userId,
                gifticonId
            ).map {
                it.toDomain()
            }
        }
    }

    override suspend fun insertUsageHistory(
        gifticonId: String,
        usageHistory: UsageHistory
    ): DbResult<Unit> {
        return runCatchingDB {
            gifticonUsageHistoryDao.insertUsageHistory(usageHistory.toEntity(gifticonId))
        }
    }
}
