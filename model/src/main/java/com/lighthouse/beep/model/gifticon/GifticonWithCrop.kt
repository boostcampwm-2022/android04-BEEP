package com.lighthouse.beep.model.gifticon

import com.lighthouse.beep.model.etc.Rectangle
import java.util.Date

data class GifticonWithCrop(
    val id: String,
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
    val croppedRect: Rectangle,
    val isUsed: Boolean,
    val createdAt: Date
)
