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

    private val brandList = arrayListOf("스타벅스", "베스킨라빈스", "BHC", "BBQ")

    var state: MutableStateFlow<MapState> = MutableStateFlow(MapState.Loading)
        private set
    var brandPlaceSearchResults = listOf<BrandPlaceInfoUiModel>()
        private set

    init {
        getBrandPlaceInfos(brandList)
    }

    fun getBrandPlaceInfos(brandList: List<String>) {
        viewModelScope.launch {
            getBrandPlaceInfosUseCase(brandList, "127.110515", "37.282778", "1000", 5)
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
