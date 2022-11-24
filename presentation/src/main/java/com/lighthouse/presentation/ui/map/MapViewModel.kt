package com.lighthouse.presentation.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.model.CustomError
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.domain.usecase.GetBrandPlaceInfosUseCase
import com.lighthouse.domain.usecase.GetUserLocationUseCase
import com.lighthouse.presentation.mapper.toPresentation
import com.lighthouse.presentation.model.BrandPlaceInfoUiModel
import com.lighthouse.presentation.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getBrandPlaceInfosUseCase: GetBrandPlaceInfosUseCase,
    private val getUserLocation: GetUserLocationUseCase
) : ViewModel() {

    private val _state: MutableSharedFlow<UiState<List<BrandPlaceInfoUiModel>>> = MutableSharedFlow()
    val state = _state.asSharedFlow()

    private val _brandInfos = mutableSetOf<BrandPlaceInfoUiModel>()
    val brandInfos: Set<BrandPlaceInfoUiModel> = _brandInfos

    // TOOD 테스트 데이터들
    val brandList = listOf("스타벅스", "베스킨라빈스", "BHC", "BBQ", "GS25", "CU", "서브웨이", "세븐일레븐", "파파존스")
    val gifticonTestData = listOf(
        Gifticon(UUID.randomUUID().toString(), "이름", "스타벅스", "스타벅스", Date(120, 20, 20), "bar", true, 1, "memo", true),
        Gifticon(UUID.randomUUID().toString(), "이름", "이름", "베스킨라빈스", Date(122, 11, 15), "bar", true, 1, "memo", true),
        Gifticon(UUID.randomUUID().toString(), "이름", "BHC", "BHC", Date(122, 5, 10), "bar", true, 1, "memo", true),
        Gifticon(UUID.randomUUID().toString(), "이름", "BBQ", "BBQ", Date(150, 10, 20), "bar", true, 1, "memo", true),
        Gifticon(UUID.randomUUID().toString(), "이름", "GS25", "GS25", Date(160, 10, 20), "bar", true, 1, "memo", true),
        Gifticon(UUID.randomUUID().toString(), "이름", "CU", "CU", Date(160, 10, 20), "bar", true, 1, "memo", true),
        Gifticon(UUID.randomUUID().toString(), "이름", "서브웨이", "서브웨이", Date(160, 10, 20), "bar", true, 1, "memo", true),
        Gifticon(UUID.randomUUID().toString(), "이름", "세븐일레븐", "세븐일레븐", Date(160, 10, 20), "bar", true, 1, "memo", true),
        Gifticon(UUID.randomUUID().toString(), "이름", "파파존스", "파파존스", Date(160, 10, 20), "bar", true, 1, "memo", true)
    )

    // TODO 현재 네이버맵에 폴리라인 그려줄려고 있는 객체입니다.
    var userLocation = MutableStateFlow(Pair(0.0, 0.0))

    fun collectLocation() {
        viewModelScope.launch {
            getUserLocation().collect { location ->
                getBrandPlaceInfos(location.longitude, location.latitude)
                userLocation.update {
                    Pair(
                        location.longitude,
                        location.latitude
                    )
                }
            }
        }
    }

    fun getBrandPlaceInfos(x: Double, y: Double) {
        viewModelScope.launch {
            _state.emit(UiState.Loading)
            runCatching { getBrandPlaceInfosUseCase(brandList, x, y, SEARCH_SIZE) }
                .mapCatching { it.toPresentation() }
                .onSuccess { brandPlaceInfos ->
                    val diffBrandPlaceInfo = brandPlaceInfos.filter {
                        brandInfos.contains(it).not()
                    }
                    _state.emit(UiState.Success(diffBrandPlaceInfo))
                    _brandInfos.addAll(brandPlaceInfos)
                }
                .onFailure { throwable ->
                    _state.emit(
                        when (throwable) {
                            CustomError.NetworkFailure -> UiState.NetworkFailure
                            CustomError.EmptyResults -> UiState.NotFoundResults
                            else -> UiState.Failure(throwable)
                        }
                    )
                }
        }
    }

    companion object {
        private const val SEARCH_SIZE = 15
    }
}
