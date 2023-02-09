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
    ): Flow<Result<Gifticon>> {
        TODO("Not yet implemented")
    }

    override fun getAllGifticons(
        userId: String,
        isUsed: Boolean,
        filterExpired: Boolean,
        sortBy: SortBy
    ): Flow<Result<List<Gifticon>>> {
        TODO("Not yet implemented")
    }

    override fun getFilteredGifticons(
        userId: String,
        isUsed: Boolean,
        filterBrand: Set<String>,
        filterExpired: Boolean,
        sortBy: SortBy
    ): Flow<Result<List<Gifticon>>> {
        TODO("Not yet implemented")
    }

    override fun getGifticonByBrand(
        userId: String,
        isUsed: Boolean,
        brand: String,
        filterExpired: Boolean
    ): Flow<Result<List<Gifticon>>> {
        TODO("Not yet implemented")
    }

    override fun getAllBrands(
        userId: String,
        isUsed: Boolean,
        filterExpired: Boolean
    ): Flow<Result<List<BrandWithGifticonCount>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getGifticonCrop(
        userId: String,
        gifticonId: String
    ): Result<GifticonWithCrop> {
        TODO("Not yet implemented")
    }

    override fun hasGifticon(
        userId: String,
        isUsed: Boolean,
        filterExpired: Boolean
    ): Flow<Result<Boolean>> {
        TODO("Not yet implemented")
    }

    override suspend fun hasGifticonBrand(
        userId: String,
        brand: String
    ): Result<Boolean> {
        TODO("Not yet implemented")
    }
}
