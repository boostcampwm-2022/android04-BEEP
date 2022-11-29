package com.lighthouse.presentation.ui.main

import com.lighthouse.domain.model.Gifticon
import com.lighthouse.presentation.model.BrandPlaceInfoUiModel

sealed class MainEvent {

    object NavigateAddGifticon : MainEvent()
    data class NavigateMap(val gifticons: List<Gifticon>, val nearBrandsInfo: List<BrandPlaceInfoUiModel>) : MainEvent()
}
