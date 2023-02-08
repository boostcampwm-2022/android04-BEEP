package com.lighthouse.repository.gifticon

import com.lighthouse.beep.model.user.UsageHistory
import com.lighthouse.domain.repository.GifticonUsageHistoryRepository
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
        TODO("Not yet implemented")
    }

    override suspend fun useCashCardGifticon(
        userId: String,
        gifticonId: String,
        amount: Int,
        usageHistory: UsageHistory
    ): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun revertUsedGifticon(
        userId: String,
        gifticonId: String
    ): Result<Unit> {
        TODO("Not yet implemented")
    }

    override fun getUsageHistory(
        userId: String,
        gifticonId: String
    ): Result<Flow<List<UsageHistory>>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertUsageHistory(
        gifticonId: String,
        usageHistory: UsageHistory
    ): Result<Unit> {
        TODO("Not yet implemented")
    }
}
