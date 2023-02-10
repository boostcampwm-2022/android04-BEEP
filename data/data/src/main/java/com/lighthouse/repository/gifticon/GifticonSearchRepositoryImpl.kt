package com.lighthouse.repository.gifticon

import com.lighthouse.beep.model.brand.BrandWithGifticonCount
import com.lighthouse.beep.model.etc.SortBy
import com.lighthouse.beep.model.gifticon.Gifticon
import com.lighthouse.beep.model.gifticon.GifticonWithCrop
import com.lighthouse.domain.repository.gifticon.GifticonSearchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GifticonSearchRepositoryImpl @Inject constructor(
    private val gifticonSearchDatabaseRepository: GifticonSearchDatabaseRepository
) : GifticonSearchRepository {

    override fun getGifticon(
        userId: String,
        gifticonId: String
    ): Flow<Gifticon> {
        return gifticonSearchDatabaseRepository.getGifticon(userId, gifticonId)
    }

    override fun getGifticons(
        userId: String,
        count: Int
    ): Flow<List<Gifticon>> {
        return gifticonSearchDatabaseRepository.getGifticons(userId, count)
    }

    override fun getAllGifticons(
        userId: String,
        isUsed: Boolean,
        filterExpired: Boolean,
        sortBy: SortBy
    ): Flow<List<Gifticon>> {
        return gifticonSearchDatabaseRepository.getAllGifticons(
            userId,
            isUsed,
            filterExpired,
            sortBy
        )
    }

    override fun getFilteredGifticons(
        userId: String,
        isUsed: Boolean,
        filterBrand: Set<String>,
        filterExpired: Boolean,
        sortBy: SortBy
    ): Flow<List<Gifticon>> {
        return gifticonSearchDatabaseRepository.getFilteredGifticons(
            userId,
            isUsed,
            filterBrand,
            filterExpired,
            sortBy
        )
    }

    override fun getGifticonByBrand(
        userId: String,
        isUsed: Boolean,
        brand: String,
        filterExpired: Boolean
    ): Flow<List<Gifticon>> {
        return gifticonSearchDatabaseRepository.getGifticonByBrand(
            userId,
            isUsed,
            brand,
            filterExpired
        )
    }

    override fun getAllBrands(
        userId: String,
        isUsed: Boolean,
        filterExpired: Boolean
    ): Flow<List<BrandWithGifticonCount>> {
        return gifticonSearchDatabaseRepository.getAllBrands(userId, isUsed, filterExpired)
    }

    override suspend fun getGifticonCrop(
        userId: String,
        gifticonId: String
    ): Result<GifticonWithCrop> {
        return gifticonSearchDatabaseRepository.getGifticonCrop(userId, gifticonId)
    }

    override fun hasGifticon(
        userId: String,
        isUsed: Boolean,
        filterExpired: Boolean
    ): Flow<Boolean> {
        return gifticonSearchDatabaseRepository.hasGifticon(userId, isUsed, filterExpired)
    }

    override suspend fun hasGifticonBrand(
        userId: String,
        brand: String
    ): Result<Boolean> {
        return gifticonSearchDatabaseRepository.hasGifticonBrand(userId, brand)
    }

    override fun getGifticonBrands(userId: String): Flow<List<String>> {
        return gifticonSearchDatabaseRepository.getGifticonBrands(userId)
    }
}
