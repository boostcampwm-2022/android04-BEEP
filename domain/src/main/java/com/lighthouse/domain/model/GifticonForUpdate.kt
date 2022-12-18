package com.lighthouse.domain.model

import java.util.Date

data class GifticonForUpdate(
    val id: String,
    val userId: String,
    val hasImage: Boolean,
    val name: String,
    val brandName: String,
    val barcode: String,
    val expiredAt: Date,
    val isCashCard: Boolean,
    val balance: Int,
    val oldCroppedUri: String,
    val croppedUri: String,
    val croppedRect: Rectangle,
    val memo: String,
    val isUsed: Boolean,
    val createdAt: Date
)
