package com.lighthouse.repository

import com.lighthouse.database.mapper.toEntity
import com.lighthouse.datasource.gifticon.GifticonImageSource
import com.lighthouse.datasource.gifticon.GifticonLocalDataSource
import com.lighthouse.domain.model.Brand
import com.lighthouse.domain.model.DbResult
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.domain.model.GifticonForAddition
import com.lighthouse.domain.model.SortBy
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

    override fun getAllGifticons(userId: String, sortBy: SortBy): Flow<DbResult<List<Gifticon>>> = flow {
        emit(DbResult.Loading)
        gifticonLocalDataSource.getAllGifticons(userId, sortBy).collect {
            emit(DbResult.Success(it))
        }
    }.catch { e ->
        emit(DbResult.Failure(e))
    }

    override fun getFilteredGifticons(
        userId: String,
        filter: Set<String>,
        sortBy: SortBy
    ): Flow<DbResult<List<Gifticon>>> = flow {
        emit(DbResult.Loading)
        gifticonLocalDataSource.getFilteredGifticons(userId, filter, sortBy).collect {
            emit(DbResult.Success(it))
        }
    }.catch { e ->
        emit(DbResult.Failure(e))
    }

    override fun getAllBrands(userId: String): Flow<DbResult<List<Brand>>> = flow {
        emit(DbResult.Loading)
        gifticonLocalDataSource.getAllBrands(userId).collect {
            emit(DbResult.Success(it))
        }
    }.catch { e ->
        emit(DbResult.Failure(e))
    }

    override suspend fun updateGifticon(gifticon: Gifticon) {
        gifticonLocalDataSource.updateGifticon(gifticon)
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
        gifticonLocalDataSource.insertUsageHistory(gifticonId, usageHistory)
    }

    override suspend fun useGifticon(gifticonId: String, usageHistory: UsageHistory) {
        gifticonLocalDataSource.useGifticon(gifticonId, usageHistory)
    }

    override suspend fun useCashCardGifticon(gifticonId: String, amount: Int, usageHistory: UsageHistory) {
        gifticonLocalDataSource.useCashCardGifticon(gifticonId, amount, usageHistory)
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

    override fun hasUsableGifticon(userId: String) = flow {
        gifticonLocalDataSource.hasUsableGifticon(userId).collect() { hasUsableGifticon ->
            emit(hasUsableGifticon)
        }
    }

    override fun getUsableGifticons(userId: String) = flow {
        emit(DbResult.Loading)
        gifticonLocalDataSource.getUsableGifticons(userId).collect { gifticons ->
            if (gifticons.isEmpty()) {
                emit(DbResult.Empty)
            } else {
                emit(DbResult.Success(gifticons.map { it.toDomain() }))
            }
        }
    }
}
