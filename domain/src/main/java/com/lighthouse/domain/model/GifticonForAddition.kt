package com.lighthouse.domain.model

import java.util.Date

data class GifticonForAddition(
    val hasImage: Boolean,
    val name: String,
    val brandName: String,
    val barcode: String,
    val expiredAt: Date,
    val isCashCard: Boolean,
    val balance: Int?,
    val originUri: String,
    val tempCroppedUri: String,
    val croppedRect: Rectangle,
    val memo: String,
)
