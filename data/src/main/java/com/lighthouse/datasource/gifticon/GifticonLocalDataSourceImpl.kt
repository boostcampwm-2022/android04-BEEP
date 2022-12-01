package com.lighthouse.datasource.gifticon

import com.lighthouse.database.dao.GifticonDao
import com.lighthouse.database.entity.GifticonEntity
import com.lighthouse.database.entity.UsageHistoryEntity
import com.lighthouse.database.mapper.toUsageHistory
import com.lighthouse.domain.model.Brand
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.domain.model.UsageHistory
import com.lighthouse.mapper.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GifticonLocalDataSourceImpl @Inject constructor(
    private val gifticonDao: GifticonDao
) : GifticonLocalDataSource {

    override fun getGifticon(id: String): Flow<Gifticon> {
        return gifticonDao.getGifticon(id).map { entity ->
            entity.toDomain()
        }
    }

    override fun getAllGifticons(userId: String): Flow<List<Gifticon>> {
        return gifticonDao.getAllGifticons(userId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getFilteredGifticons(userId: String, filter: Set<String>): Flow<List<Gifticon>> {
        return gifticonDao.getFilteredGifticons(userId, filter).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getAllBrands(userId: String): Flow<List<Brand>> {
        return gifticonDao.getAllBrands(userId)
    }

    override suspend fun updateGifticon(gifticon: GifticonEntity) {
        gifticonDao.updateGifticon(gifticon)
    }

    override suspend fun insertGifticons(gifticons: List<GifticonEntity>) {
        gifticonDao.insertGifticon(*gifticons.toTypedArray())
    }

    override suspend fun useGifticon(usageHistory: UsageHistoryEntity) {
        gifticonDao.useGifticonTransaction(usageHistory)
    }

    override suspend fun useCashCardGifticon(amount: Int, usageHistory: UsageHistoryEntity) {
        gifticonDao.useCashCardGifticonTransaction(amount, usageHistory)
    }

    override suspend fun unUseGifticon(gifticonId: String) {
        gifticonDao.unUseGifticon(gifticonId)
    }

    override fun getUsageHistory(gifticonId: String): Flow<List<UsageHistory>> {
        return gifticonDao.getUsageHistory(gifticonId).map { list ->
            list.map { entity ->
                entity.toUsageHistory()
            }
        }
    }

    override suspend fun insertUsageHistory(usageHistory: UsageHistoryEntity) {
        gifticonDao.insertUsageHistory(usageHistory)
    }

    override fun getGifticonByBrand(brand: String): Flow<List<GifticonEntity>> {
        return gifticonDao.getGifticonByBrand(brand)
    }
}
