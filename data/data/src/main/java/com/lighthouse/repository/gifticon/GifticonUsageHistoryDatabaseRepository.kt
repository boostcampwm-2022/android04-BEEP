package com.lighthouse.repository.gifticon

import com.lighthouse.beep.model.result.DbResult
import com.lighthouse.beep.model.user.UsageHistory
import kotlinx.coroutines.flow.Flow

interface GifticonUsageHistoryDatabaseRepository {
    suspend fun useGifticon(
        userId: String,
        gifticonId: String,
        usageHistory: UsageHistory
    ): DbResult<Unit>

    suspend fun useCashCardGifticon(
        userId: String,
        gifticonId: String,
        amount: Int,
        usageHistory: UsageHistory
    ): DbResult<Unit>

    suspend fun revertUsedGifticon(
        userId: String,
        gifticonId: String
    ): DbResult<Unit>

    fun getUsageHistory(
        userId: String,
        gifticonId: String
    ): DbResult<Flow<List<UsageHistory>>>

    suspend fun insertUsageHistory(
        gifticonId: String,
        usageHistory: UsageHistory
    ): DbResult<Unit>
}
