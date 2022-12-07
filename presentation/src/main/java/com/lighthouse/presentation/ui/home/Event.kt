package com.lighthouse.presentation.ui.home

import com.lighthouse.domain.model.Gifticon
import com.lighthouse.presentation.model.BrandPlaceInfoUiModel

sealed class Event {

    data class NavigateMap(val gifticons: List<Gifticon>, val nearBrandsInfo: List<BrandPlaceInfoUiModel>) : Event()
    object RequestLocationPermissionCheck : Event()
}
