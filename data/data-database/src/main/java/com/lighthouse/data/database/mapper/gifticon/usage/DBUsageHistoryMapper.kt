package com.lighthouse.data.database.mapper.gifticon.usage

import com.lighthouse.beep.model.location.VertexLocation
import com.lighthouse.beep.model.user.UsageHistory
import com.lighthouse.data.database.entity.DBUsageHistoryEntity

internal fun List<DBUsageHistoryEntity>.toDomain(): List<UsageHistory> {
    return map {
        it.toDomain()
    }
}

internal fun DBUsageHistoryEntity.toDomain(): UsageHistory {
    return UsageHistory(
        date = date,
        location = VertexLocation(longitude, latitude),
        amount = amount
    )
}
