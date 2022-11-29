package com.lighthouse.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.model.BeepError
import com.lighthouse.domain.model.DbResult
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.domain.usecase.GetBrandPlaceInfosUseCase
import com.lighthouse.domain.usecase.GetGifticonsUseCase
import com.lighthouse.domain.usecase.GetUserLocationUseCase
import com.lighthouse.presentation.mapper.toPresentation
import com.lighthouse.presentation.model.BrandPlaceInfoUiModel
import com.lighthouse.presentation.ui.common.UiState
import com.lighthouse.presentation.util.TimeCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getGifticonUseCase: GetGifticonsUseCase,
    private val getUserLocation: GetUserLocationUseCase,
    private val getBrandPlaceInfosUseCase: GetBrandPlaceInfosUseCase
) : ViewModel() {

    private val gifticons = getGifticonUseCase().stateIn(viewModelScope, SharingStarted.Eagerly, DbResult.Loading)

    private val allBrands = gifticons.transform { gifticons ->
        if (gifticons is DbResult.Success) {
            emit(gifticons.data.map { it.brand }.distinct())
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val allGifticons = gifticons.transform { gifticons ->
        if (gifticons is DbResult.Success) {
            emit(
                gifticons.data
                    .filter { TimeCalculator.formatDdayToInt(it.expireAt.time) > 0 }
                    .sortedBy { TimeCalculator.formatDdayToInt(it.expireAt.time) }
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _nearGifticon: MutableStateFlow<UiState<List<Gifticon>>> = MutableStateFlow(UiState.Loading)
    val nearGifticon = _nearGifticon.asStateFlow()

    var nearBrandsInfo = listOf<BrandPlaceInfoUiModel>()
        private set

    init {
        viewModelScope.launch {
            getUserLocation().collect { location ->
                getNearBrands(location.longitude, location.latitude)
            }
        }
    }

    private fun getNearBrands(x: Double, y: Double) {
        viewModelScope.launch {
            _nearGifticon.emit(UiState.Loading)
            runCatching { getBrandPlaceInfosUseCase(allBrands.value, x, y, SEARCH_SIZE) }
                .mapCatching { it.toPresentation() }
                .onSuccess { brands ->
                    nearBrandsInfo = brands
                    val gifticonFilterResult =
                        allGifticons.value.filter { gifticon -> brands.map { it.brand }.contains(gifticon.brand) }
                    _nearGifticon.value = UiState.Success(gifticonFilterResult)
                }
                .onFailure { throwable ->
                    _nearGifticon.value = when (throwable) {
                        BeepError.NetworkFailure -> UiState.NetworkFailure
                        else -> UiState.Failure
                    }
                }
        }
    }

    companion object {
        private const val SEARCH_SIZE = 15
    }
}
