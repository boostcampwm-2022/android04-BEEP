package com.lighthouse.datasource.gifticon

import com.lighthouse.database.entity.GifticonEntity
import com.lighthouse.database.entity.UsageHistoryEntity
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.domain.model.UsageHistory
import kotlinx.coroutines.flow.Flow

interface GifticonLocalDataSource {
    fun getGifticon(id: String): Flow<Gifticon>
    suspend fun insertGifticons(gifticons: List<GifticonEntity>)
    suspend fun updateGifticon(gifticon: GifticonEntity)
    suspend fun useGifticon(usageHistory: UsageHistoryEntity)
    suspend fun useCashCardGifticon(amount: Int, usageHistory: UsageHistoryEntity)
    suspend fun unUseGifticon(gifticonId: String)

    fun getUsageHistory(gifticonId: String): Flow<List<UsageHistory>>
    suspend fun insertUsageHistory(usageHistory: UsageHistoryEntity)
    fun getGifticonByBrand(brand: String): Flow<List<GifticonEntity>>
    fun getAllGifticons(): Flow<List<GifticonEntity>>
}
