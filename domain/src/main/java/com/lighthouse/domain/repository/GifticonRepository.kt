package com.lighthouse.domain.repository

import com.lighthouse.domain.model.Brand
import com.lighthouse.domain.model.DbResult
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.domain.model.GifticonForAddition
import com.lighthouse.domain.model.GifticonForUpdate
import com.lighthouse.domain.model.SortBy
import com.lighthouse.domain.model.UsageHistory
import kotlinx.coroutines.flow.Flow

interface GifticonRepository {

    fun getGifticon(id: String): Flow<DbResult<Gifticon>>
    fun getAllGifticons(userId: String, sortBy: SortBy = SortBy.DEADLINE): Flow<DbResult<List<Gifticon>>>
    fun getAllUsedGifticons(userId: String): Flow<DbResult<List<Gifticon>>>
    fun getFilteredGifticons(
        userId: String,
        filter: Set<String>,
        sortBy: SortBy = SortBy.DEADLINE,
    ): Flow<DbResult<List<Gifticon>>>

    fun getAllBrands(userId: String): Flow<DbResult<List<Brand>>>
    suspend fun saveGifticons(userId: String, gifticonForAdditions: List<GifticonForAddition>)
    suspend fun getGifticonCrop(userId: String, id: String): GifticonForUpdate?
    suspend fun updateGifticon(gifticonForUpdate: GifticonForUpdate)
    fun getUsageHistory(gifticonId: String): Flow<DbResult<List<UsageHistory>>>
    suspend fun saveUsageHistory(gifticonId: String, usageHistory: UsageHistory)
    suspend fun useGifticon(gifticonId: String, usageHistory: UsageHistory)
    suspend fun useCashCardGifticon(gifticonId: String, amount: Int, usageHistory: UsageHistory)
    suspend fun unUseGifticon(gifticonId: String)
    suspend fun removeGifticon(gifticonId: String)
    fun getGifticonByBrand(brand: String): Flow<DbResult<List<Gifticon>>>
    fun hasUsableGifticon(userId: String): Flow<Boolean>
    fun getUsableGifticons(userId: String): Flow<DbResult<List<Gifticon>>>
    suspend fun hasGifticonBrand(brand: String): Boolean
    suspend fun moveUserIdGifticon(oldUserId: String, newUserId: String)
    fun getGifticonBrands(userId: String): Flow<DbResult<List<String>>>
    fun getSomeGifticons(userId: String, count: Int): Flow<DbResult<List<Gifticon>>>
}
