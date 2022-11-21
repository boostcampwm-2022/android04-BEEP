package com.lighthouse.presentation.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.model.CustomError
import com.lighthouse.domain.usecase.GetBrandPlaceInfosUseCase
import com.lighthouse.presentation.mapper.toPresentation
import com.lighthouse.presentation.model.BrandPlaceInfoUiModel
import com.lighthouse.presentation.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getBrandPlaceInfosUseCase: GetBrandPlaceInfosUseCase
) : ViewModel() {

    private val brandList = listOf("스타벅스", "베스킨라빈스", "BHC", "BBQ", "GS25", "CU", "아파트", "어린이집")

    var state: MutableStateFlow<UiState<List<BrandPlaceInfoUiModel>>> = MutableStateFlow(UiState.Loading)
        private set

    fun getBrandPlaceInfos(x: Double, y: Double) {
        viewModelScope.launch {
            state.value = UiState.Loading
            getBrandPlaceInfosUseCase(brandList, x.toString(), y.toString(), 5)
                .mapCatching { it.toPresentation() }
                .onSuccess { brandPlaceInfos ->
                    state.value = UiState.Success(brandPlaceInfos)
                }
                .onFailure { throwable ->
                    state.value = when (throwable) {
                        CustomError.NetworkFailure -> UiState.NetworkFailure
                        CustomError.EmptyResults -> UiState.NotFoundResults
                        else -> UiState.Failure(throwable)
                    }
                }
        }
    }
}
