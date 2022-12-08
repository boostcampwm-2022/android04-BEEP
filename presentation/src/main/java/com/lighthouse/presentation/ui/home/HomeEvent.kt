package com.lighthouse.presentation.ui.home

import com.lighthouse.domain.model.Gifticon
import com.lighthouse.presentation.model.BrandPlaceInfoUiModel

sealed class HomeEvent {

    data class NavigateMap(val gifticons: List<Gifticon>, val nearBrandsInfo: List<BrandPlaceInfoUiModel>) : HomeEvent()
    object RequestLocationPermissionCheck : HomeEvent()
}
