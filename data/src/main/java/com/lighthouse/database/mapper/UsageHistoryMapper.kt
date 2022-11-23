package com.lighthouse.database.mapper

import com.lighthouse.database.entity.UsageHistoryEntity
import com.lighthouse.domain.model.UsageHistory

fun UsageHistoryEntity.toUsageHistory(): UsageHistory {
    return UsageHistory(
        date = date,
        address = address,
        amount = amount
    )
}

fun UsageHistory.toUsageHistoryEntity(gifticonId: String): UsageHistoryEntity {
    return UsageHistoryEntity(
        gifticonId = gifticonId,
        date = date,
        address = address,
        amount = amount
    )
}
