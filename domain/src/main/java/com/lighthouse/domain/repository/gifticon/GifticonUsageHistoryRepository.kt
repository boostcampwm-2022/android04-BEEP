package com.lighthouse.domain.repository.gifticon

import com.lighthouse.beep.model.user.UsageHistory
import kotlinx.coroutines.flow.Flow

interface GifticonUsageHistoryRepository {

    suspend fun useGifticon(
        userId: String,
        gifticonId: String,
        usageHistory: UsageHistory
    ): Result<Unit>

    suspend fun useCashCardGifticon(
        userId: String,
        gifticonId: String,
        amount: Int,
        usageHistory: UsageHistory
    ): Result<Unit>

    suspend fun revertUsedGifticon(
        userId: String,
        gifticonId: String
    ): Result<Unit>

    fun getUsageHistory(
        userId: String,
        gifticonId: String
    ): Flow<List<UsageHistory>>

    suspend fun insertUsageHistory(
        gifticonId: String,
        usageHistory: UsageHistory
    ): Result<Unit>
}
