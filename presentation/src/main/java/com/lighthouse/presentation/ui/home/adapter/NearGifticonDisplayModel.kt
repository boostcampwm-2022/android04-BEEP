package com.lighthouse.presentation.ui.home.adapter

import com.lighthouse.presentation.R
import com.lighthouse.presentation.model.GifticonWithDistanceUIModel
import com.lighthouse.presentation.util.resource.UIText

class NearGifticonDisplayModel(
    val item: GifticonWithDistanceUIModel
) {
    fun distance(): UIText {
        val meter = calculate(item.distance)
        return when (meter > MINIMUM_MITER) {
            true -> UIText.StringResource(R.string.home_near_gifticon_distance, meter)
            false -> UIText.StringResource(R.string.home_near_gifticon_announce)
        }
    }

    private fun calculate(distance: Int): Int {
        val div = distance / MINIMUM_CRITERIA_MITER
        return div * MINIMUM_CRITERIA_MITER
    }

    companion object {
        private const val MINIMUM_CRITERIA_MITER = 10
        private const val MINIMUM_MITER = 100
    }
}
