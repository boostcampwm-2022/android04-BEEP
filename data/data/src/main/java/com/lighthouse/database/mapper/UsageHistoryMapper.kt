package com.lighthouse.database.mapper

import com.lighthouse.beep.model.location.VertexLocation
import com.lighthouse.beep.model.user.UsageHistory
import com.lighthouse.database.entity.UsageHistoryEntity

fun UsageHistoryEntity.toUsageHistory(): UsageHistory {
    return UsageHistory(
        date = date,
        location = VertexLocation(longitude, latitude),
        amount = amount
    )
}

fun UsageHistory.toUsageHistoryEntity(gifticonId: String): UsageHistoryEntity {
    return UsageHistoryEntity(
        gifticonId = gifticonId,
        date = date,
        longitude = location?.longitude ?: 0f.toDouble(),
        latitude = location?.latitude ?: 0f.toDouble(),
        amount = amount
    )
}
