package com.lighthouse.datasource.gifticon

import com.lighthouse.database.dao.GifticonDao
import com.lighthouse.database.entity.GifticonEntity
import com.lighthouse.database.entity.GifticonWithCrop
import com.lighthouse.database.mapper.toHistory
import com.lighthouse.database.mapper.toHistoryEntity
import com.lighthouse.domain.model.Brand
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.domain.model.History
import com.lighthouse.domain.model.SortBy
import com.lighthouse.domain.util.currentTime
import com.lighthouse.domain.util.isExpired
import com.lighthouse.domain.util.today
import com.lighthouse.mapper.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GifticonLocalDataSourceImpl @Inject constructor(
    private val gifticonDao: GifticonDao,
) : GifticonLocalDataSource {

    override fun getGifticon(id: String): Flow<Gifticon> {
        return gifticonDao.getGifticon(id).map { entity ->
            entity.toDomain()
        }
    }

    override fun getAllGifticons(userId: String, sortBy: SortBy): Flow<List<Gifticon>> {
        val gifticons = when (sortBy) {
            SortBy.DEADLINE -> gifticonDao.getAllGifticonsSortByDeadline(userId)
            SortBy.RECENT -> gifticonDao.getAllGifticons(userId)
        }
        return gifticons.map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getAllUsedGifticons(userId: String): Flow<List<Gifticon>> {
        return gifticonDao.getAllUsedGifticons(userId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getFilteredGifticons(
        userId: String,
        filter: Set<String>,
        sortBy: SortBy,
    ): Flow<List<Gifticon>> {
        val upperFilter = filter.map { it.uppercase() }.toSet()
        val gifticons = when (sortBy) {
            SortBy.DEADLINE -> gifticonDao.getFilteredGifticonsSortByDeadline(userId, upperFilter)
            SortBy.RECENT -> gifticonDao.getFilteredGifticons(userId, upperFilter)
        }
        return gifticons.map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getAllBrands(userId: String, filterExpired: Boolean): Flow<List<Brand>> {
        return gifticonDao.getAllGifticons(userId).map {
            if (filterExpired) {
                it.filterNot { entity ->
                    entity.expireAt.isExpired()
                }
            } else {
                it
            }.groupBy { entity ->
                entity.brand.uppercase()
            }.map { entry ->
                Brand(entry.key.uppercase(), entry.value.size)
            }
        }
    }

    override suspend fun getGifticonCrop(userId: String, gifticonId: String): GifticonWithCrop? {
        return gifticonDao.getGifticonWithCrop(userId, gifticonId)
    }

    override suspend fun updateGifticon(gifticonWithCrop: GifticonWithCrop) {
        val gifticonId = gifticonWithCrop.id
        val latestBalance = gifticonDao.getLatestAmount(gifticonWithCrop.id)

        gifticonDao.updateGifticonWithCropTransaction(gifticonWithCrop)

        // 금액 수정 시 기록 남기기
        if (gifticonWithCrop.balance != latestBalance) {
            val history = History.ModifyAmount(currentTime, gifticonId)
            gifticonDao.insertUsageHistory(history.toHistoryEntity(gifticonWithCrop.balance))
        }
    }

    override suspend fun insertGifticons(gifticons: List<GifticonWithCrop>) {
        gifticonDao.insertGifticonWithCropTransaction(gifticons)
        gifticons.forEach { gifticon ->
            val history = History.Init(currentTime, gifticon.id)
            gifticonDao.insertUsageHistory(history.toHistoryEntity(gifticon.balance))
        }
    }

    override suspend fun useGifticon(gifticonId: String, history: History.Use) {
        val latestAmount = gifticonDao.getLatestAmount(gifticonId)
        gifticonDao.useGifticonTransaction(history.toHistoryEntity(latestAmount))
    }

    override suspend fun useCashCardGifticon(
        gifticonId: String,
        amount: Int,
        history: History.UseCashCard,
    ) {
        val balance = gifticonDao.getLatestAmount(gifticonId)

        balance ?: throw IllegalStateException("balance should not be null")
        assert(balance >= amount) // 사용할 금액이 잔액보다 많으면 안된다

        gifticonDao.useCashCardGifticonTransaction(
            amount,
            history.toHistoryEntity(balance - amount),
        )
    }

    override suspend fun unUseGifticon(history: History.CancelUsage) {
        val secondLatestAmount = gifticonDao.getSecondLatestAmount(history.gifticonId)
        gifticonDao.unUseGifticonTransaction(history.toHistoryEntity(secondLatestAmount))
    }

    override suspend fun removeGifticon(gifticonId: String) {
        gifticonDao.removeGifticon(gifticonId)
    }

    override fun getHistory(gifticonId: String): Flow<List<History>> {
        return gifticonDao.getUsageHistory(gifticonId).map { list ->
            list.map { entity ->
                entity.toHistory()
            }
        }
    }

    override fun getGifticonByBrand(brand: String): Flow<List<GifticonEntity>> {
        return gifticonDao.getGifticonByBrand(brand)
    }

    override fun hasUsableGifticon(userId: String): Flow<Boolean> {
        return gifticonDao.hasUsableGifticon(userId, today)
    }

    override fun getUsableGifticons(userId: String): Flow<List<GifticonEntity>> {
        return gifticonDao.getAllUsableGifticons(userId, today)
    }

    override suspend fun hasGifticonBrand(brand: String): Boolean {
        return gifticonDao.hasGifticonBrand(brand)
    }

    override suspend fun moveUserIdGifticon(oldUserId: String, newUserId: String) {
        gifticonDao.moveUserIdGifticon(oldUserId, newUserId)
    }

    override fun getGifticonBrands(userId: String): Flow<List<String>> {
        return gifticonDao.getGifticonBrands(userId, today)
    }

    override fun getSomeGifticons(userId: String, count: Int): Flow<List<GifticonEntity>> {
        return gifticonDao.getSomeGifticons(userId, today, count)
    }
}
