package com.lighthouse.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.LocationConverter
import com.lighthouse.domain.VertexLocation
import com.lighthouse.domain.model.BeepError
import com.lighthouse.domain.model.DbResult
import com.lighthouse.domain.usecase.GetBrandPlaceInfosUseCase
import com.lighthouse.domain.usecase.GetGifticonsUseCase
import com.lighthouse.domain.usecase.GetUserLocationUseCase
import com.lighthouse.domain.usecase.HasLocationPermissionsUseCase
import com.lighthouse.domain.usecase.UpdateLocationPermissionUseCase
import com.lighthouse.presentation.mapper.toPresentation
import com.lighthouse.presentation.model.BrandPlaceInfoUiModel
import com.lighthouse.presentation.model.GifticonUiModel
import com.lighthouse.presentation.ui.common.UiState
import com.lighthouse.presentation.util.flow.MutableEventFlow
import com.lighthouse.presentation.util.flow.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getGifticonUseCase: GetGifticonsUseCase,
    hasLocationPermissionsUseCase: HasLocationPermissionsUseCase,
    private val getUserLocationUseCase: GetUserLocationUseCase,
    private val getBrandPlaceInfosUseCase: GetBrandPlaceInfosUseCase,
    private val updateLocationPermissionUseCase: UpdateLocationPermissionUseCase
) : ViewModel() {

    private var locationFlow: Job? = null

    private val _eventFlow = MutableEventFlow<HomeEvent>()
    val eventFlow = _eventFlow.asEventFlow()

    private val gifticons =
        getGifticonUseCase.getUsableGifticons().stateIn(viewModelScope, SharingStarted.Eagerly, DbResult.Loading)

    private val allBrands = gifticons.transform { gifticons ->
        if (gifticons is DbResult.Success) {
            emit(gifticons.data.map { it.brand }.distinct())
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val gifticonsMap = gifticons.transform { gifticons ->
        if (gifticons is DbResult.Success) {
            val gifticonGroup = gifticons.data.groupBy { it.brand }
            emit(gifticonGroup)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

    val expiredGifticon = gifticonsMap.transform { gifticons ->
        val gifticonFlatten = gifticons.values.flatten()
        val gifticonSize =
            if (gifticonFlatten.size < EXPIRED_GIFTICON_LIST_MAX_SIZE) gifticonFlatten.size else EXPIRED_GIFTICON_LIST_MAX_SIZE
        emit(gifticonFlatten.slice(0 until gifticonSize))
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _uiState: MutableStateFlow<UiState<Unit>> = MutableStateFlow(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var nearBrandsInfo = listOf<BrandPlaceInfoUiModel>()

    private lateinit var recentLocation: VertexLocation

    val hasLocationPermission = hasLocationPermissionsUseCase()

    val isShimmer = MutableStateFlow(false)

    val isEmptyNearBrands = MutableStateFlow(false)

    private val _nearGifticons: MutableStateFlow<List<GifticonUiModel>> = MutableStateFlow(emptyList())
    val nearGifticons = _nearGifticons.asStateFlow()

    init {
        viewModelScope.launch {
            hasLocationPermission.collectLatest { result ->
                Timber.tag("TAG").d("${javaClass.simpleName} result -> $result")
                if (result) observeLocationFlow()
            }
        }
    }

    private fun observeLocationFlow() {
        Timber.tag("TAG").d("${javaClass.simpleName} observeLocationFlow ${locationFlow?.isActive}")
        if (locationFlow?.isActive == true) return

        locationFlow = getUserLocationUseCase().onEach { location ->
            Timber.tag("TAG").d("${javaClass.simpleName} location collect -> $location")
            recentLocation = location
            getNearBrands(location.longitude, location.latitude)
        }.launchIn(viewModelScope)
    }

    private fun getNearBrands(x: Double, y: Double) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            isEmptyNearBrands.value = false
            isShimmer.value = true
            runCatching { getBrandPlaceInfosUseCase(allBrands.value, x, y, SEARCH_SIZE) }
                .mapCatching { brand -> brand.toPresentation() }
                .onSuccess { brands ->
                    nearBrandsInfo = brands.sortedBy { diffLocation(it, recentLocation) }
                    _nearGifticons.value = nearBrandsInfo.distinctBy { it.brand }.mapNotNull { placeInfo ->
                        gifticonsMap.value[placeInfo.brand]?.first()
                            ?.toPresentation(diffLocation(placeInfo, recentLocation))
                    }
                    when (_nearGifticons.value.isEmpty()) {
                        true -> {
                            _uiState.emit(UiState.NotFoundResults)
                            isEmptyNearBrands.value = true
                        }
                        false -> _uiState.emit(UiState.Success(Unit))
                    }
                }
                .onFailure { throwable ->
                    _uiState.emit(
                        when (throwable) {
                            BeepError.NetworkFailure -> UiState.NetworkFailure
                            else -> UiState.Failure
                        }
                    )
                }
            isShimmer.value = false
        }
    }

    private fun diffLocation(
        brandLocation: BrandPlaceInfoUiModel,
        currentLocation: VertexLocation
    ) = LocationConverter.locationDistance(
        brandLocation.x.toDouble(),
        brandLocation.y.toDouble(),
        currentLocation.longitude,
        currentLocation.latitude
    )

    fun gotoMap() {
        viewModelScope.launch {
            _eventFlow.emit(HomeEvent.NavigateMap(gifticonsMap.value.values.flatten(), nearBrandsInfo))
        }
    }

    fun changeLocationPermission(hasLocationPermission: Boolean) {
        updateLocationPermissionUseCase(hasLocationPermission)
    }

    fun requestLocationPermission() {
        viewModelScope.launch {
            _eventFlow.emit(HomeEvent.RequestLocationPermissionCheck)
        }
    }

    companion object {
        private const val SEARCH_SIZE = 15
        private const val EXPIRED_GIFTICON_LIST_MAX_SIZE = 7
    }
}
