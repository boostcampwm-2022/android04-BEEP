package com.lighthouse.presentation.mapper

import com.lighthouse.domain.model.Gifticon
import com.lighthouse.presentation.model.GifticonUiModel
import com.lighthouse.presentation.ui.widget.GifticonWidgetData

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

fun Gifticon.toWidgetModel(distance: Double, categoryName: String): GifticonWidgetData {
    return GifticonWidgetData(
        id = this.id,
        category = categoryName,
        name = this.name,
        brand = this.brand,
        distance = distance.toInt()
    )
}
