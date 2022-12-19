package com.lighthouse.presentation.ui.home

import com.lighthouse.presentation.model.BrandPlaceInfoUiModel
import com.lighthouse.presentation.model.GifticonUIModel

sealed class HomeEvent {

    data class NavigateMap(val gifticons: List<GifticonUIModel>, val nearBrandsInfo: List<BrandPlaceInfoUiModel>) : HomeEvent()
    object RequestLocationPermissionCheck : HomeEvent()
}
