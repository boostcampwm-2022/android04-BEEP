package com.lighthouse.data.database.mapper.gifticon.usage

import com.lighthouse.beep.model.user.UsageHistory
import com.lighthouse.data.database.entity.DBUsageHistoryEntity

internal fun UsageHistory.toEntity(gifticonId: String): DBUsageHistoryEntity {
    return DBUsageHistoryEntity(
        gifticonId = gifticonId,
        date = date,
        longitude = location?.longitude ?: 0f.toDouble(),
        latitude = location?.latitude ?: 0f.toDouble(),
        amount = amount
    )
}
