package com.lighthouse.presentation.mapper

import com.lighthouse.domain.model.Gifticon
import com.lighthouse.presentation.R
import com.lighthouse.presentation.model.GifticonUiModel
import com.lighthouse.presentation.util.resource.UIText

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
        distance = calculate(distance),
        uiText = setDistanceToUiText(distance)
    )
}

private const val MINIMUM_CRITERIA_MITER = 10
private const val MINIMUM_MITER = 100

private fun setDistanceToUiText(distance: Double): UIText.StringResource {
    val meter = calculate(distance)

    return when (meter > MINIMUM_MITER) {
        true -> UIText.StringResource(R.string.home_near_gifticon_distance, meter)
        false -> UIText.StringResource(R.string.home_near_gifticon_announce)
    }
}

private fun calculate(distance: Double): Int {
    val div = distance.toInt() / MINIMUM_CRITERIA_MITER
    return div * MINIMUM_CRITERIA_MITER
}
