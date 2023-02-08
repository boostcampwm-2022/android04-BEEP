package com.lighthouse.repository.gifticon

import com.lighthouse.beep.model.brand.BrandWithGifticonCount
import com.lighthouse.beep.model.etc.SortBy
import com.lighthouse.beep.model.gifticon.Gifticon
import com.lighthouse.beep.model.gifticon.GifticonWithCrop
import com.lighthouse.beep.model.result.DbResult
import kotlinx.coroutines.flow.Flow

interface GifticonSearchDatabaseRepository {
    fun getGifticon(
        userId: String,
        gifticonId: String
    ): DbResult<Flow<Gifticon>>

    fun getAllGifticons(
        userId: String,
        isUsed: Boolean,
        filterExpired: Boolean,
        sortBy: SortBy = SortBy.DEADLINE
    ): DbResult<Flow<List<Gifticon>>>

    fun getFilteredGifticons(
        userId: String,
        isUsed: Boolean,
        filterBrand: Set<String>,
        filterExpired: Boolean,
        sortBy: SortBy = SortBy.DEADLINE
    ): DbResult<Flow<List<Gifticon>>>

    fun getGifticonByBrand(
        userId: String,
        isUsed: Boolean,
        brand: String,
        filterExpired: Boolean
    ): DbResult<Flow<List<Gifticon>>>

    fun getAllBrands(
        userId: String,
        isUsed: Boolean,
        filterExpired: Boolean
    ): DbResult<Flow<List<BrandWithGifticonCount>>>

    suspend fun getGifticonCrop(
        userId: String,
        gifticonId: String
    ): DbResult<GifticonWithCrop>

    fun hasGifticon(
        userId: String,
        isUsed: Boolean,
        filterExpired: Boolean
    ): DbResult<Flow<Boolean>>

    suspend fun hasGifticonBrand(
        userId: String,
        brand: String
    ): DbResult<Boolean>
}
