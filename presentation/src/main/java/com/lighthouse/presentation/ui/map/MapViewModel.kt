package com.lighthouse.presentation.ui.map

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.LocationConverter
import com.lighthouse.domain.VertexLocation
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
import kotlinx.coroutines.flow.combine
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

    private val resultGifticons =
        getGifticonUseCase.getUsableGifticons().stateIn(viewModelScope, SharingStarted.Eagerly, DbResult.Loading)

    private val allGifticons = resultGifticons.transform { gifticons ->
        if (gifticons is DbResult.Success) {
            emit(gifticons.data.sortedBy { TimeCalculator.formatDdayToInt(it.expireAt.time) })
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val allBrands = allGifticons.transform { gifticons ->
        emit(gifticons.map { it.brandLowerName }.distinct())
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _gifticonData = MutableStateFlow<List<Gifticon>>(emptyList())
    val gifticonData = _gifticonData.asStateFlow()

    private val _event = MutableEventFlow<MapEvent>()
    val event = _event.asEventFlow()

    private val _widgetBrand = MutableEventFlow<String>()
    val widgetBrand = _widgetBrand.asEventFlow()

    private var prevVertex = MutableStateFlow<VertexLocation?>(null)
    var viewPagerFocus = false
        private set

    private var removeMarker = allBrands.transform {
        val brands = it ?: return@transform
        val removeMarkers = markerHolder.filter { marker -> brands.contains(marker.captionText.lowercase()).not() }
        emit(removeMarkers)
    }

    init {
        setRemoveMarker()
        val isFirstLoadData = checkHomeData(savedStateHandle)
        combineLocationGifticon()
        collectLocation(isFirstLoadData)
    }

    private fun checkHomeData(savedStateHandle: SavedStateHandle): Boolean {
        var isFirstLoadData = true
        val nearBrands = savedStateHandle.get<List<BrandPlaceInfoUiModel>>(Extras.KEY_NEAR_BRANDS)
        val nearGifticons = savedStateHandle.get<List<Gifticon>>(Extras.KEY_NEAR_GIFTICONS)
        viewModelScope.launch {
            if (nearBrands.isNullOrEmpty() || nearGifticons.isNullOrEmpty()) {
                isFirstLoadData = false
            } else {
                // homeActivity에서 받은 데이터가 있는 경우에만 실행
                _brandInfos.addAll(nearBrands)
                _state.emit(UiState.Success(nearBrands))
                updateGifticons()
            }
            val brand = savedStateHandle.get<String>(Extras.KEY_WIDGET_BRAND) ?: return@launch
            _widgetBrand.emit(brand)
        }
        return isFirstLoadData
    }

    private fun setRemoveMarker() {
        viewModelScope.launch {
            removeMarker.collectLatest {
                _markerHolder.removeAll(it.toSet())
                _event.emit(MapEvent.DeleteMarker(it))
                resetMarker()
                updatePagerFocus(false)
                updateGifticons()
            }
        }
    }

    private fun collectLocation(isFirstLoadData: Boolean) {
        viewModelScope.launch {
            var isNeededFirstLoading = isFirstLoadData
            getUserLocation().collectLatest { location ->
                if (isNeededFirstLoading) {
                    isNeededFirstLoading = false
                    return@collectLatest
                }
                val currentDms = LocationConverter.setDmsLocation(location)
                val prevDms = prevVertex.value?.let { LocationConverter.setDmsLocation(it) }

                if (prevDms != currentDms) prevVertex.value = location
            }
        }
    }

    private fun combineLocationGifticon() {
        viewModelScope.launch {
            prevVertex.combine(allBrands) { location, _ ->
                location
            }.collectLatest { location ->
                location ?: run {
                    updateGifticons()
                    return@collectLatest
                }
                val brands = allBrands.value ?: return@collectLatest

                _state.emit(UiState.Loading)
                getBrandPlaceInfosUseCase(brands, location.longitude, location.latitude, SEARCH_SIZE)
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
    }

    fun updateFocusMarker(marker: Marker) {
        recentSelectedMarker = marker
        focusMarker = marker
    }

    fun updateMarkers(brandMarkers: List<Marker>) {
        _markerHolder.addAll(brandMarkers)
        updateGifticons()
    }

    fun updateGifticons() {
        val brandName = focusMarker.captionText.lowercase()
        _gifticonData.value = when (brandName.isEmpty()) {
            true -> {
                allGifticons.value.filter { gifticon ->
                    brandInfos.map { it.brandLowerName }.contains(gifticon.brandLowerName)
                }
            }
            false -> allGifticons.value.filter { it.brandLowerName == brandName }
        }
    }

    fun resetMarker() {
        focusMarker = Marker()
    }

    fun gotoHome() {
        viewModelScope.launch {
            _event.emit(MapEvent.NavigateHome)
        }
    }

    fun updatePagerFocus(isPagerFocus: Boolean) {
        viewPagerFocus = isPagerFocus
    }

    companion object {
        private const val SEARCH_SIZE = 15
    }
}
