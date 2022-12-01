package com.lighthouse.repository

import com.lighthouse.database.mapper.toEntity
import com.lighthouse.database.mapper.toGifticonEntity
import com.lighthouse.database.mapper.toUsageHistoryEntity
import com.lighthouse.datasource.gifticon.GifticonImageSource
import com.lighthouse.datasource.gifticon.GifticonLocalDataSource
import com.lighthouse.domain.model.DbResult
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.domain.model.GifticonForAddition
import com.lighthouse.domain.model.UsageHistory
import com.lighthouse.domain.repository.GifticonRepository
import com.lighthouse.mapper.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GifticonRepositoryImpl @Inject constructor(
    private val gifticonLocalDataSource: GifticonLocalDataSource,
    private val gifticonImageSource: GifticonImageSource
) : GifticonRepository {

    override fun getGifticon(id: String): Flow<DbResult<Gifticon>> = flow {
        emit(DbResult.Loading)
        gifticonLocalDataSource.getGifticon(id).collect {
            emit(DbResult.Success(it))
        }
    }.catch { e ->
        emit(DbResult.Failure(e))
    }

    override fun getAllGifticons(): Flow<DbResult<List<Gifticon>>> = flow {
        emit(DbResult.Loading)
        gifticonLocalDataSource.getAllGifticons().collect {
            emit(DbResult.Success(it))
        }
    }.catch { e ->
        emit(DbResult.Failure(e))
    }

    override suspend fun updateGifticon(gifticon: Gifticon) {
        gifticonLocalDataSource.updateGifticon(gifticon.toGifticonEntity())
    }

    override suspend fun saveGifticons(userId: String, gifticons: List<GifticonForAddition>) {
        val newGifticons = gifticons.map {
            it.toEntity(userId)
        }
        gifticonLocalDataSource.insertGifticons(newGifticons)

        for ((newGifticon, addition) in newGifticons.zip(gifticons)) {
            gifticonImageSource.saveImage(newGifticon.id, addition)
        }
    }

    override fun getUsageHistory(gifticonId: String): Flow<DbResult<List<UsageHistory>>> = flow {
        emit(DbResult.Loading)
        gifticonLocalDataSource.getUsageHistory(gifticonId).collect {
            if (it.isEmpty()) {
                emit(DbResult.Empty)
            } else {
                emit(DbResult.Success(it))
            }
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

    override fun getGifticonByBrand(brand: String) = flow {
        emit(DbResult.Loading)
        gifticonLocalDataSource.getGifticonByBrand(brand).collect { gifticons ->
            if (gifticons.isEmpty()) {
                emit(DbResult.Empty)
            } else {
                emit(DbResult.Success(gifticons.map { it.toDomain() }))
            }
        }
    }

    override fun hasVariableGifticon() = flow {
        gifticonLocalDataSource.hasVariableGifticon().collect() { hasVariableGifticon ->
            if (hasVariableGifticon) {
                emit(true)
            } else {
                emit(false)
            }
        }
    }
}
