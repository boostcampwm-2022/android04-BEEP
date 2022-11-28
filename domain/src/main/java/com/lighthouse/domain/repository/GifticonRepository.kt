package com.lighthouse.domain.repository

import com.lighthouse.domain.model.DbResult
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.domain.model.UsageHistory
import kotlinx.coroutines.flow.Flow

interface GifticonRepository {

    fun getGifticon(id: String): Flow<DbResult<Gifticon>>
    suspend fun saveGifticons(gifticons: List<Gifticon>)
    suspend fun updateGifticon(gifticon: Gifticon)

    fun getUsageHistory(gifticonId: String): Flow<DbResult<List<UsageHistory>>>
    suspend fun saveUsageHistory(gifticonId: String, usageHistory: UsageHistory)
    suspend fun useGifticon(gifticonId: String, usageHistory: UsageHistory)
    suspend fun useCashCardGifticon(gifticonId: String, amount: Int, usageHistory: UsageHistory)
    suspend fun unUseGifticon(gifticonId: String)
    fun getGifticonByBrand(brand: String): Flow<DbResult<List<Gifticon>>>
    fun getAllGifticon(): Flow<DbResult<List<Gifticon>>>
}
