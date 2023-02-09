package com.lighthouse.data.database.repository.gifticon

import com.lighthouse.beep.model.brand.BrandWithGifticonCount
import com.lighthouse.beep.model.etc.SortBy
import com.lighthouse.beep.model.gifticon.Gifticon
import com.lighthouse.beep.model.gifticon.GifticonWithCrop
import com.lighthouse.data.database.dao.GifticonSearchDao
import com.lighthouse.data.database.exception.DBNotFoundException
import com.lighthouse.data.database.ext.runCatchingDB
import com.lighthouse.data.database.mapper.gifticon.edit.toDomain
import com.lighthouse.data.database.mapper.gifticon.search.toDomain
import com.lighthouse.repository.gifticon.GifticonSearchDatabaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

internal class GifticonSearchDatabaseRepositoryImpl @Inject constructor(
    private val gifticonSearchDao: GifticonSearchDao
) : GifticonSearchDatabaseRepository {

    override fun getGifticon(
        userId: String,
        gifticonId: String
    ): Flow<Gifticon> {
        return gifticonSearchDao.getGifticon(userId, gifticonId).map {
            it.toDomain()
        }
    }

    override fun getAllGifticons(
        userId: String,
        isUsed: Boolean,
        filterExpired: Boolean,
        sortBy: SortBy
    ): Flow<List<Gifticon>> {
        val gifticons = when (filterExpired) {
            true -> when (sortBy) {
                SortBy.RECENT -> gifticonSearchDao.getAllGifticonsSortByRecent(
                    userId,
                    isUsed,
                    Date()
                )

                SortBy.DEADLINE -> gifticonSearchDao.getAllGifticonsSortByDeadline(
                    userId,
                    isUsed,
                    Date()
                )
            }

            false -> when (sortBy) {
                SortBy.RECENT -> gifticonSearchDao.getAllGifticonsSortByRecent(
                    userId,
                    isUsed
                )

                SortBy.DEADLINE -> gifticonSearchDao.getAllGifticonsSortByDeadline(
                    userId,
                    isUsed
                )
            }
        }
        return gifticons.map {
            it.toDomain()
        }
    }

    override fun getFilteredGifticons(
        userId: String,
        isUsed: Boolean,
        filterBrand: Set<String>,
        filterExpired: Boolean,
        sortBy: SortBy
    ): Flow<List<Gifticon>> {
        val upperFilterBrand = filterBrand.map {
            it.uppercase()
        }.toSet()

        val gifticons = when (filterExpired) {
            true -> when (sortBy) {
                SortBy.RECENT -> gifticonSearchDao.getFilteredGifticonsSortByRecent(
                    userId,
                    isUsed,
                    upperFilterBrand,
                    Date()
                )

                SortBy.DEADLINE -> gifticonSearchDao.getFilteredGifticonsSortByDeadline(
                    userId,
                    isUsed,
                    upperFilterBrand,
                    Date()
                )
            }

            false -> when (sortBy) {
                SortBy.RECENT -> gifticonSearchDao.getFilteredGifticonsSortByRecent(
                    userId,
                    isUsed,
                    upperFilterBrand
                )

                SortBy.DEADLINE -> gifticonSearchDao.getFilteredGifticonsSortByDeadline(
                    userId,
                    isUsed,
                    upperFilterBrand
                )
            }
        }

        return gifticons.map {
            it.toDomain()
        }
    }

    override fun getAllBrands(
        userId: String,
        isUsed: Boolean,
        filterExpired: Boolean
    ): Flow<List<BrandWithGifticonCount>> {
        val brandWithGifticonCount = when (filterExpired) {
            true -> gifticonSearchDao.getAllBrands(userId, isUsed, Date())
            false -> gifticonSearchDao.getAllBrands(userId, isUsed)
        }

        return brandWithGifticonCount.map {
            it.toDomain()
        }
    }

    override fun getGifticonByBrand(
        userId: String,
        isUsed: Boolean,
        brand: String,
        filterExpired: Boolean
    ): Flow<List<Gifticon>> {
        val gifticons = when (filterExpired) {
            true -> gifticonSearchDao.getGifticonByBrand(userId, isUsed, brand)
            false -> gifticonSearchDao.getGifticonByBrand(userId, isUsed, brand, Date())
        }
        return gifticons.map {
            it.toDomain()
        }
    }

    override suspend fun getGifticonCrop(
        userId: String,
        gifticonId: String
    ): Result<GifticonWithCrop> {
        return runCatchingDB {
            gifticonSearchDao.getGifticonWithCrop(userId, gifticonId)?.toDomain()
                ?: throw DBNotFoundException("해당 기프티콘을 찾을 수 없습니다.")
        }
    }

    override fun hasGifticon(
        userId: String,
        isUsed: Boolean,
        filterExpired: Boolean
    ): Flow<Boolean> {
        return when (filterExpired) {
            true -> gifticonSearchDao.hasGifticon(userId, isUsed)
            false -> gifticonSearchDao.hasGifticon(userId, isUsed, Date())
        }
    }

    override suspend fun hasGifticonBrand(userId: String, brand: String): Result<Boolean> {
        return runCatchingDB {
            gifticonSearchDao.hasGifticonBrand(userId, brand)
        }
    }
}
