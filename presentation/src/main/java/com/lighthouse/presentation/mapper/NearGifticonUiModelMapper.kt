package com.lighthouse.presentation.mapper

import com.lighthouse.domain.model.Gifticon
import com.lighthouse.presentation.model.GifticonUiModel

fun Gifticon.toPresentation(distance: Double): GifticonUiModel {
    return GifticonUiModel(
        id = this.id,
        userId = this.userId,
        hasImage = this.hasImage,
        name = this.name,
        brand = this.brand,
        expireAt = this.expireAt,
        balance = this.balance,
        isUsed = this.isUsed,
        distance = distance.toInt()
    )
}
