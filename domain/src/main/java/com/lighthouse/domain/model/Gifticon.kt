package com.lighthouse.domain.model

import java.util.Date

data class Gifticon(
    val id: String,
    val userId: String,
    val hasImage: Boolean,
    val name: String,
    val brand: String,
    val expireAt: Date,
    val barcode: String,
    val isCashCard: Boolean,
    val balance: Int,
    val memo: String,
    val isUsed: Boolean
)
