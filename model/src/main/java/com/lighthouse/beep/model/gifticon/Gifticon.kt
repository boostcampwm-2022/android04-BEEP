package com.lighthouse.beep.model.gifticon

import java.util.Date

data class Gifticon(
    val id: String,
    val createdAt: Date,
    val userId: String,
    val hasImage: Boolean,
    val croppedUri: String,
    val name: String,
    val brand: String,
    val expireAt: Date,
    val barcode: String,
    val isCashCard: Boolean,
    val balance: Int,
    val memo: String,
    val isUsed: Boolean
)
