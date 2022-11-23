package com.lighthouse.repository

import com.lighthouse.database.mapper.toGifticonEntity
import com.lighthouse.database.mapper.toUsageHistoryEntity
import com.lighthouse.datasource.gifticon.GifticonLocalDataSource
import com.lighthouse.domain.model.DbResult
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.domain.model.UsageHistory
import com.lighthouse.domain.repository.GifticonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GifticonRepositoryImpl @Inject constructor(
    private val gifticonLocalDataSource: GifticonLocalDataSource
) : GifticonRepository {

    override fun getGifticon(id: String): Flow<DbResult<Gifticon>> = flow {
        emit(DbResult.Loading)
        gifticonLocalDataSource.getGifticon(id).collect {
            emit(DbResult.Success(it))
        }
    }.catch { e ->
        emit(DbResult.Failure(e))
    }

    override suspend fun saveGifticons(gifticons: List<Gifticon>) {
        gifticonLocalDataSource.insertGifticons(
            gifticons.map {
                it.toGifticonEntity()
            }
        )
    }

    override fun getUsageHistory(gifticonId: String): Flow<DbResult<List<UsageHistory>>> = flow {
        emit(DbResult.Loading)
        gifticonLocalDataSource.getUsageHistory(gifticonId).collect {
            emit(DbResult.Success(it))
        }
    }.catch { e ->
        emit(DbResult.Failure(e))
    }

    override suspend fun saveUsageHistory(gifticonId: String, usageHistory: UsageHistory) {
        gifticonLocalDataSource.insertUsageHistory(usageHistory.toUsageHistoryEntity(gifticonId))
    }

    override suspend fun useGifticon(gifticonId: String, usageHistory: UsageHistory) {
        gifticonLocalDataSource.useGifticon(usageHistory.toUsageHistoryEntity(gifticonId))
    }

    override suspend fun useCashCardGifticon(gifticonId: String, amount: Int, usageHistory: UsageHistory) {
        gifticonLocalDataSource.useCashCardGifticon(amount, usageHistory.toUsageHistoryEntity(gifticonId))
    }

    override suspend fun unUseGifticon(gifticonId: String) {
        gifticonLocalDataSource.unUseGifticon(gifticonId)
    }
}
