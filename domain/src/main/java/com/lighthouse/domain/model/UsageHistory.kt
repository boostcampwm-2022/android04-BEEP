package com.lighthouse.domain.model

import com.lighthouse.domain.VertexLocation
import java.util.Date

data class UsageHistory(
    val date: Date,
    val location: VertexLocation,
    val amount: Int
)
