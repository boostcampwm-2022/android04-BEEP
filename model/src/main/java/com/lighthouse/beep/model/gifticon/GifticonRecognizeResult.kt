package com.lighthouse.beep.model.gifticon

import com.lighthouse.beep.model.etc.Rectangle
import java.util.Date

data class GifticonRecognizeResult(
    val name: String,
    val brandName: String,
    val barcode: String,
    val expiredAt: Date,
    val isCashCard: Boolean,
    val balance: Int,
    val originUri: String,
    val croppedUri: String,
    val croppedRect: Rectangle
)
