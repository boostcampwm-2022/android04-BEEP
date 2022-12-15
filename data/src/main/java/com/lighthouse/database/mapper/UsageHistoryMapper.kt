package com.lighthouse.database.mapper

import com.lighthouse.database.entity.UsageHistoryEntity
import com.lighthouse.domain.VertexLocation
import com.lighthouse.domain.model.UsageHistory

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
        longitude = location.longitude,
        latitude = location.latitude,
        amount = amount
    )
}
