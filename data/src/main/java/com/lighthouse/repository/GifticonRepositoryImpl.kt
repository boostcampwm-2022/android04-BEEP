package com.lighthouse.repository

import android.net.Uri
import com.lighthouse.database.mapper.toDomain
import com.lighthouse.database.mapper.toEntity
import com.lighthouse.datasource.gifticon.GifticonImageSource
import com.lighthouse.datasource.gifticon.GifticonLocalDataSource
import com.lighthouse.domain.model.Brand
import com.lighthouse.domain.model.DbResult
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.domain.model.GifticonForAddition
import com.lighthouse.domain.model.GifticonForUpdate
import com.lighthouse.domain.model.SortBy
import com.lighthouse.domain.model.UsageHistory
import com.lighthouse.domain.repository.GifticonRepository
import com.lighthouse.mapper.toDomain
import com.lighthouse.model.GifticonImageResult
import com.lighthouse.util.UUID
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

    override fun getAllGifticons(userId: String, sortBy: SortBy): Flow<DbResult<List<Gifticon>>> =
        flow {
            emit(DbResult.Loading)
            gifticonLocalDataSource.getAllGifticons(userId, sortBy).collect {
                emit(DbResult.Success(it))
            }
        }.catch { e ->
            emit(DbResult.Failure(e))
        }

    override fun getAllUsedGifticons(userId: String): Flow<DbResult<List<Gifticon>>> = flow {
        emit(DbResult.Loading)
        gifticonLocalDataSource.getAllUsedGifticons(userId).collect {
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

    override fun getAllBrands(userId: String, filterExpired: Boolean): Flow<DbResult<List<Brand>>> =
        flow {
            emit(DbResult.Loading)
            gifticonLocalDataSource.getAllBrands(userId, filterExpired).collect {
                emit(DbResult.Success(it))
            }
        }.catch { e ->
            emit(DbResult.Failure(e))
        }

    override suspend fun getGifticonCrop(userId: String, id: String): GifticonForUpdate? {
        return gifticonLocalDataSource.getGifticonCrop(userId, id)?.toDomain()
    }

    override suspend fun updateGifticon(gifticonForUpdate: GifticonForUpdate) {
        var croppedUri: Uri? = null
        if (gifticonForUpdate.isUpdatedImage) {
            croppedUri = gifticonImageSource.updateImage(
                gifticonForUpdate.id,
                Uri.parse(gifticonForUpdate.oldCroppedUri),
                Uri.parse(gifticonForUpdate.croppedUri)
            )
        }
        gifticonLocalDataSource.updateGifticon(gifticonForUpdate.toEntity(croppedUri))
    }

    override suspend fun saveGifticons(
        userId: String,
        gifticonForAdditions: List<GifticonForAddition>
    ) {
        val newGifticons = gifticonForAdditions.map { gifticonForAddition ->
            val id = UUID.generate()
            var result: GifticonImageResult? = null
            if (gifticonForAddition.hasImage) {
                result = gifticonImageSource.saveImage(
                    id,
                    Uri.parse(gifticonForAddition.originUri),
                    Uri.parse(gifticonForAddition.tempCroppedUri)
                )
            }
            gifticonForAddition.toEntity(id, userId, result)
        }
        gifticonLocalDataSource.insertGifticons(newGifticons)
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

    override suspend fun useCashCardGifticon(
        gifticonId: String,
        amount: Int,
        usageHistory: UsageHistory
    ) {
        gifticonLocalDataSource.useCashCardGifticon(gifticonId, amount, usageHistory)
    }

    override suspend fun unUseGifticon(gifticonId: String) {
        gifticonLocalDataSource.unUseGifticon(gifticonId)
    }

    override suspend fun removeGifticon(gifticonId: String) {
        gifticonLocalDataSource.removeGifticon(gifticonId)
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
        gifticonLocalDataSource.hasUsableGifticon(userId).collect { hasUsableGifticon ->
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

    override suspend fun hasGifticonBrand(brand: String) =
        gifticonLocalDataSource.hasGifticonBrand(brand)

    override suspend fun moveUserIdGifticon(oldUserId: String, newUserId: String) {
        gifticonLocalDataSource.moveUserIdGifticon(oldUserId, newUserId)
    }

    override fun getGifticonBrands(userId: String): Flow<DbResult<List<String>>> = flow {
        emit(DbResult.Loading)

        gifticonLocalDataSource.getGifticonBrands(userId).collect { brands ->
            if (brands.isEmpty()) {
                emit(DbResult.Empty)
            } else {
                emit(DbResult.Success(brands))
            }
        }
    }
}
