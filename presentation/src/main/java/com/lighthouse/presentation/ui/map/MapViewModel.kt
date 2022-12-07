package com.lighthouse.presentation.ui.map

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.Dms
import com.lighthouse.domain.DmsLocation
import com.lighthouse.domain.LocationConverter
import com.lighthouse.domain.model.BeepError
import com.lighthouse.domain.model.DbResult
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.domain.usecase.GetBrandPlaceInfosUseCase
import com.lighthouse.domain.usecase.GetGifticonsUseCase
import com.lighthouse.domain.usecase.GetUserLocationUseCase
import com.lighthouse.presentation.extra.Extras
import com.lighthouse.presentation.mapper.toPresentation
import com.lighthouse.presentation.model.BrandPlaceInfoUiModel
import com.lighthouse.presentation.ui.common.UiState
import com.lighthouse.presentation.util.TimeCalculator
import com.lighthouse.presentation.util.flow.MutableEventFlow
import com.lighthouse.presentation.util.flow.asEventFlow
import com.naver.maps.map.overlay.Marker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    getGifticonUseCase: GetGifticonsUseCase,
    savedStateHandle: SavedStateHandle,
    private val getBrandPlaceInfosUseCase: GetBrandPlaceInfosUseCase,
    private val getUserLocation: GetUserLocationUseCase
) : ViewModel() {

    private var prevLocation = DmsLocation(Dms(0, 0, 0), Dms(0, 0, 0))

    private val _state: MutableEventFlow<UiState<List<BrandPlaceInfoUiModel>>> = MutableEventFlow()
    val state = _state.asEventFlow()

    var recentSelectedMarker = Marker()
        private set

    var focusMarker = Marker()
        private set

    private val _markerHolder = mutableSetOf<Marker>()
    val markerHolder: Set<Marker> = _markerHolder

    private val _brandInfos = mutableSetOf<BrandPlaceInfoUiModel>()
    val brandInfos: Set<BrandPlaceInfoUiModel> = _brandInfos

    private val gifticons =
        getGifticonUseCase.getUsableGifticons().stateIn(viewModelScope, SharingStarted.Eagerly, DbResult.Loading)

    val allGifticons = gifticons.transform { gifticons ->
        if (gifticons is DbResult.Success) {
            emit(gifticons.data.sortedBy { TimeCalculator.formatDdayToInt(it.expireAt.time) })
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val allBrands = allGifticons.transform { gifticons ->
        emit(gifticons.map { it.brand }.distinct())
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _gifticonData = MutableStateFlow<List<Gifticon>>(emptyList())
    val gifticonData = _gifticonData.asStateFlow()

    private val widgetBrand = savedStateHandle.get<String>(Extras.WIDGET_BRAND_KEY)

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    init {
        var isFirstLoadData = true
        val nearBrands = savedStateHandle.get<List<BrandPlaceInfoUiModel>>(Extras.KEY_NEAR_BRANDS)
        val nearGifticons = savedStateHandle.get<List<Gifticon>>(Extras.KEY_NEAR_GIFTICONS)

        if (nearBrands.isNullOrEmpty() || nearGifticons.isNullOrEmpty()) {
            isFirstLoadData = false
        } else {
            // homeActivity에서 받은 데이터가 있는 경우에만 실행
            viewModelScope.launch {
                _brandInfos.addAll(nearBrands)
                _state.emit(UiState.Success(nearBrands))
                updateGifticons()
            }
        }
        collectLocation(isFirstLoadData)
        viewModelScope.launch {
            val brand = widgetBrand ?: return@launch
            _event.emit(Event.NavigateBrand(brand))
        }
    }

    private fun collectLocation(isFirstLoadData: Boolean) {
        var isNeededFirstLoading = isFirstLoadData
        viewModelScope.launch {
            getUserLocation().collectLatest { location ->
                if (isNeededFirstLoading) {
                    isNeededFirstLoading = false
                    return@collectLatest
                }
                val currentLocation = LocationConverter.setDmsLocation(location)
                if (prevLocation != currentLocation) {
                    prevLocation = currentLocation
                    getBrandPlaceInfos(location.longitude, location.latitude)
                }
            }
        }
    }

    private fun getBrandPlaceInfos(x: Double, y: Double) {
        viewModelScope.launch {
            _state.emit(UiState.Loading)
            runCatching { getBrandPlaceInfosUseCase(allBrands.value, x, y, SEARCH_SIZE) }
                .mapCatching { it.toPresentation() }
                .onSuccess { brandPlaceInfos ->
                    val diffBrandPlaceInfo = brandPlaceInfos.filter {
                        brandInfos.contains(it).not()
                    }
                    _brandInfos.addAll(brandPlaceInfos)
                    when (brandInfos.isEmpty()) {
                        true -> _state.emit(UiState.NotFoundResults)
                        false -> {
                            _state.emit(UiState.Success(diffBrandPlaceInfo))
                            updateGifticons()
                        }
                    }
                }
                .onFailure { throwable ->
                    Timber.tag("TAG").d("${javaClass.simpleName} throwable -> $throwable")
                    _state.emit(
                        when (throwable) {
                            BeepError.NetworkFailure -> UiState.NetworkFailure
                            else -> UiState.Failure
                        }
                    )
                }
        }
    }

    fun updateFocusMarker(marker: Marker) {
        recentSelectedMarker = marker
        focusMarker = marker
    }

    fun updateMarkers(brandMarkers: List<Marker>) {
        _markerHolder.addAll(brandMarkers)
    }

    fun updateGifticons() {
        val brandName = focusMarker.captionText
        Timber.tag("TAG").d("${javaClass.simpleName} brandName -> $brandName")
        _gifticonData.value = when (brandName.isEmpty()) {
            true -> {
                allGifticons.value.filter { gifticon ->
                    brandInfos.map { it.brand }.contains(gifticon.brand)
                }
            }
            false -> allGifticons.value.filter { it.brand == brandName }
        }
    }

    fun resetMarker() {
        focusMarker = Marker()
    }

    companion object {
        private const val SEARCH_SIZE = 15
    }
}
