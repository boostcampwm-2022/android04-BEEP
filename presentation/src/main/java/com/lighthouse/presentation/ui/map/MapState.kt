package com.lighthouse.presentation.ui.map

import com.lighthouse.presentation.model.BrandPlaceInfoUiModel

sealed class MapState {
    data class Success(val brandPlaceInfoUiModel: List<BrandPlaceInfoUiModel>) : MapState()
    object Loading : MapState()
    object NotFoundSearchResults : MapState()
    object NetworkFailure : MapState()
    object Failure : MapState()
}
