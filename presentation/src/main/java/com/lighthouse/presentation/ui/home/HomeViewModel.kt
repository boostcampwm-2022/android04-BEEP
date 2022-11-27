package com.lighthouse.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.model.BeepError
import com.lighthouse.domain.model.DbResult
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.domain.usecase.GetGifticonsUseCase
import com.lighthouse.domain.usecase.GetNearBrandsUseCase
import com.lighthouse.domain.usecase.GetUserLocationUseCase
import com.lighthouse.domain.usecase.SaveGifticonsUseCase
import com.lighthouse.presentation.ui.common.UiState
import com.lighthouse.presentation.util.UUID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val saveGifticonUseCase: SaveGifticonsUseCase,
    private val getGifticonUseCase: GetGifticonsUseCase,
    private val getUserLocation: GetUserLocationUseCase,
    private val getNearBrandsUseCase: GetNearBrandsUseCase
) : ViewModel() {

    private val gifticons = getGifticonUseCase().stateIn(viewModelScope, SharingStarted.Eagerly, DbResult.Loading)

    private val allBrands = gifticons.transform { gifticons ->
        if (gifticons is DbResult.Success) {
            emit(gifticons.data.map { it.brand }.distinct())
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val allGifticons = gifticons.transform { gifticons ->
        if (gifticons is DbResult.Success) {
            emit(gifticons.data)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _state: MutableSharedFlow<UiState<List<Gifticon>>> = MutableSharedFlow()
    val state = _state.asSharedFlow()

    init {
        // TODO 테스트 더미 데이터
        val gifticonTestData = listOf(
            Gifticon(UUID.generate(), "이름", "스타벅스", "스타벅스", Date(120, 20, 20), "bar", true, 1, "memo", true),
            Gifticon(UUID.generate(), "이름", "이름", "베스킨라빈스", Date(122, 11, 15), "bar", true, 1, "memo", true),
            Gifticon(UUID.generate(), "이름", "BHC", "BHC", Date(122, 5, 10), "bar", true, 1, "memo", true),
            Gifticon(UUID.generate(), "이름", "GS25", "GS25", Date(160, 10, 20), "bar", true, 1, "memo", true),
            Gifticon(UUID.generate(), "이름", "CU", "CU", Date(160, 10, 20), "bar", true, 1, "memo", true),
            Gifticon(UUID.generate(), "이름", "서브웨이", "서브웨이", Date(160, 10, 20), "bar", true, 1, "memo", true),
            Gifticon(UUID.generate(), "이름", "세븐일레븐", "세븐일레븐", Date(160, 10, 20), "bar", true, 1, "memo", true),
            Gifticon(UUID.generate(), "이름", "파파존스", "파파존스", Date(160, 10, 20), "bar", true, 1, "memo", true)
        )
        viewModelScope.launch {
            saveGifticonUseCase(gifticonTestData)
        }
        viewModelScope.launch {
            getUserLocation().collect { location ->
                getNearBrands(location.longitude, location.latitude)
            }
        }
    }

    private fun getNearBrands(x: Double, y: Double) {
        viewModelScope.launch {
            Timber.tag("TAG").d("${javaClass.simpleName} 시작 brands -> ${allBrands.value}")
            _state.emit(UiState.Loading)
            runCatching { getNearBrandsUseCase(allBrands.value, x, y) }
                .onSuccess { nearBrands ->
                    Timber.tag("TAG").d("${javaClass.simpleName} nearBrands -> $nearBrands")
                    allGifticons.value.filter { gifticon ->
                        nearBrands.contains(gifticon.brand)
                    }
                }
                .onFailure { throwable ->
                    Timber.tag("TAG").d("${javaClass.simpleName} map throw -> $throwable")
                    when (throwable) {
                        BeepError.NetworkFailure -> UiState.NetworkFailure
                        else -> UiState.Failure(throwable)
                    }
                }
        }
    }
}