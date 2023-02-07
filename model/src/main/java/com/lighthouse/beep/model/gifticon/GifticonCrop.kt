package com.lighthouse.beep.model.gifticon

import com.lighthouse.beep.model.etc.Rectangle

data class GifticonCrop(
    val gifticonId: String,
    val rect: Rectangle
) {
    val originPath = "origin$gifticonId"
}
