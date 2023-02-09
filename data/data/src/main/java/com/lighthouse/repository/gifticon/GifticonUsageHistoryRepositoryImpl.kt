package com.lighthouse.repository.gifticon

import com.lighthouse.beep.model.user.UsageHistory
import com.lighthouse.domain.repository.gifticon.GifticonUsageHistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GifticonUsageHistoryRepositoryImpl @Inject constructor(
    private val gifticonUsageHistoryDatabaseRepository: GifticonUsageHistoryDatabaseRepository
) : GifticonUsageHistoryRepository {

    override suspend fun useGifticon(
        userId: String,
        gifticonId: String,
        usageHistory: UsageHistory
    ): Result<Unit> {
        return gifticonUsageHistoryDatabaseRepository.useGifticon(
            userId,
            gifticonId,
            usageHistory
        )
    }

    override suspend fun useCashCardGifticon(
        userId: String,
        gifticonId: String,
        amount: Int,
        usageHistory: UsageHistory
    ): Result<Unit> {
        return gifticonUsageHistoryDatabaseRepository.useCashCardGifticon(
            userId,
            gifticonId,
            amount,
            usageHistory
        )
    }

    override suspend fun revertUsedGifticon(
        userId: String,
        gifticonId: String
    ): Result<Unit> {
        return gifticonUsageHistoryDatabaseRepository.revertUsedGifticon(
            userId,
            gifticonId
        )
    }

    override fun getUsageHistory(
        userId: String,
        gifticonId: String
    ): Flow<List<UsageHistory>> {
        return gifticonUsageHistoryDatabaseRepository.getUsageHistory(
            userId,
            gifticonId
        )
    }

    override suspend fun insertUsageHistory(
        gifticonId: String,
        usageHistory: UsageHistory
    ): Result<Unit> {
        return gifticonUsageHistoryDatabaseRepository.insertUsageHistory(
            gifticonId,
            usageHistory
        )
    }
}
