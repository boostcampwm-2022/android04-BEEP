package com.lighthouse.presentation.mapper

import com.lighthouse.domain.model.Gifticon
import com.lighthouse.presentation.R
import com.lighthouse.presentation.model.GifticonUiModel
import com.lighthouse.presentation.util.resource.UIText

fun Gifticon.toUiModel(distance: Double): GifticonUiModel {
    return GifticonUiModel(
        id = this.id,
        userId = this.userId,
        hasImage = this.hasImage,
        name = this.name,
        brand = this.brand,
        expireAt = this.expireAt,
        balance = this.balance,
        isUsed = this.isUsed,
        distance = calculateDistance(distance)
    )
}

private const val MINIMUM_MITER = 100

private fun calculateDistance(distance: Double): UIText.StringResource {
    val div = distance.toInt() / MINIMUM_MITER
    val meter = div * MINIMUM_MITER

    return when (meter > MINIMUM_MITER) {
        true -> UIText.StringResource(R.string.home_near_gifticon_distance, meter)
        false -> UIText.StringResource(R.string.home_near_gifticon_announce)
    }
}
