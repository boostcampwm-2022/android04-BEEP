package com.lighthouse.beep.model.user

import com.lighthouse.beep.model.location.VertexLocation
import java.util.Date

data class UsageHistory(
    val date: Date,
    val location: VertexLocation?,
    val amount: Int
)
