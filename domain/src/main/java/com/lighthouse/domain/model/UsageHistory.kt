package com.lighthouse.domain.model

import java.util.Date

data class UsageHistory(
    val date: Date,
    val address: String,
    val amount: Int
)
