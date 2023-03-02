package com.lighthouse.datasource.gifticon

import com.lighthouse.database.entity.GifticonEntity
import com.lighthouse.database.entity.GifticonWithCrop
import com.lighthouse.domain.model.Brand
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.domain.model.History
import com.lighthouse.domain.model.SortBy
import kotlinx.coroutines.flow.Flow

interface GifticonLocalDataSource {

    fun getGifticon(id: String): Flow<Gifticon>
    fun getAllGifticons(userId: String, sortBy: SortBy = SortBy.DEADLINE): Flow<List<Gifticon>>
    fun getAllUsedGifticons(userId: String): Flow<List<Gifticon>>
    fun getFilteredGifticons(
        userId: String,
        filter: Set<String>,
        sortBy: SortBy = SortBy.DEADLINE,
    ): Flow<List<Gifticon>>

    fun getAllBrands(userId: String, filterExpired: Boolean): Flow<List<Brand>>
    suspend fun getGifticonCrop(userId: String, gifticonId: String): GifticonWithCrop?
    suspend fun insertGifticons(gifticons: List<GifticonWithCrop>)
    suspend fun updateGifticon(gifticonWithCrop: GifticonWithCrop)
    suspend fun useGifticon(gifticonId: String, history: History.Use)
    suspend fun useCashCardGifticon(gifticonId: String, amount: Int, history: History.UseCashCard)
    suspend fun unUseGifticon(history: History.CancelUsage)
    suspend fun removeGifticon(gifticonId: String)
    fun getHistory(gifticonId: String): Flow<List<History>>
    suspend fun resetHistoryButInit(gifticonId: String)
    fun getGifticonByBrand(brand: String): Flow<List<GifticonEntity>>
    fun hasUsableGifticon(userId: String): Flow<Boolean>
    fun getUsableGifticons(userId: String): Flow<List<GifticonEntity>>
    suspend fun hasGifticonBrand(brand: String): Boolean
    suspend fun moveUserIdGifticon(oldUserId: String, newUserId: String)
    fun getGifticonBrands(userId: String): Flow<List<String>>
    fun getSomeGifticons(userId: String, count: Int): Flow<List<GifticonEntity>>
}
