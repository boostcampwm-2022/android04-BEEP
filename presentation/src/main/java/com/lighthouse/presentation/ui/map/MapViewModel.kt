package com.lighthouse.presentation.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.model.CustomError
import com.lighthouse.domain.usecase.GetBrandPlaceInfosUseCase
import com.lighthouse.presentation.mapper.toPresentation
import com.lighthouse.presentation.model.BrandPlaceInfoUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getBrandPlaceInfosUseCase: GetBrandPlaceInfosUseCase
) : ViewModel() {

    private val brandList = listOf("스타벅스", "베스킨라빈스", "BHC", "BBQ", "GS25", "CU", "아파트", "어린이집")

    var state: MutableStateFlow<MapState> = MutableStateFlow(MapState.Loading)
        private set
    var brandPlaceSearchResults = listOf<BrandPlaceInfoUiModel>()
        private set

    fun getBrandPlaceInfos(x: Double, y: Double) {
        viewModelScope.launch {
            getBrandPlaceInfosUseCase(brandList, x.toString(), y.toString(), 5)
                .mapCatching { it.toPresentation() }
                .onSuccess { brandPlaceInfos ->
                    brandPlaceSearchResults = brandPlaceInfos
                    state.value = MapState.Success
                }
                .onFailure { throwable ->
                    when (throwable) {
                        CustomError.NetworkFailure -> state.value = MapState.NetworkFailure
                        CustomError.NotFoundBrandPlaceInfos -> state.value = MapState.NotFoundSearchResults
                        else -> state.value = MapState.Failure
                    }
                }
        }
    }
}
