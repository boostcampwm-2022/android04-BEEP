package com.lighthouse.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.Dms
import com.lighthouse.domain.DmsLocation
import com.lighthouse.domain.LocationConverter.diffLocation
import com.lighthouse.domain.LocationConverter.setDmsLocation
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

    private var prevLocation = DmsLocation(Dms(0, 0, 0), Dms(0, 0, 0))
    private var locationFlow: Job? = null

    private val _eventFlow = MutableEventFlow<Event>()
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

    val expiredGifticon = gifticons.transform { gifticons ->
        if (gifticons is DbResult.Success) {
            val data = gifticons.data
            val gifticonSize =
                if (data.size < EXPIRED_GIFTICON_LIST_MAX_SIZE) data.size else EXPIRED_GIFTICON_LIST_MAX_SIZE
            emit(data.slice(0 until gifticonSize))
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _uiState: MutableStateFlow<UiState<Unit>> = MutableStateFlow(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var nearBrandsInfo = listOf<BrandPlaceInfoUiModel>()

    val hasLocationPermission = hasLocationPermissionsUseCase()
    val isShimmer = MutableStateFlow(false)
    val isEmptyNearBrands = MutableStateFlow(false)

    private val _nearGifticons: MutableStateFlow<List<GifticonUiModel>> = MutableStateFlow(emptyList())
    val nearGifticons = _nearGifticons.asStateFlow()

    init {
        setLocationFlowJob()
    }

    private fun setLocationFlowJob() {
        viewModelScope.launch {
            hasLocationPermission.collectLatest { result ->
                if (result) observeLocationFlow()
            }
        }
    }

    private fun observeLocationFlow() {
        if (locationFlow?.isActive == true) return

        locationFlow = viewModelScope.launch {
            getUserLocationUseCase().collectLatest { location ->
                val currentLocation = setDmsLocation(location)
                if (prevLocation != currentLocation) {
                    prevLocation = currentLocation
                    getNearBrands(location.longitude, location.latitude)
                }
            }
        }
    }

    private fun getNearBrands(x: Double, y: Double) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            isEmptyNearBrands.value = false
            isShimmer.value = true
            runCatching { getBrandPlaceInfosUseCase(allBrands.value, x, y, SEARCH_SIZE) }
                .mapCatching { brand -> brand.toPresentation() }
                .onSuccess { brands ->
                    nearBrandsInfo = brands.sortedBy { diffLocation(it.x, it.y, x, y) }
                    _nearGifticons.value = nearBrandsInfo.distinctBy { it.brand }.mapNotNull { placeInfo ->
                        gifticonsMap.value[placeInfo.brand]?.first()
                            ?.toPresentation(diffLocation(placeInfo.x, placeInfo.y, x, y))
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
                    Timber.tag("TAG").d("${javaClass.simpleName} homeViewModel Error -> $throwable")
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

    fun gotoMap() {
        viewModelScope.launch {
            _eventFlow.emit(Event.NavigateMap(gifticonsMap.value.values.flatten(), nearBrandsInfo))
        }
    }

    fun changeLocationPermission(hasLocationPermission: Boolean) {
        updateLocationPermissionUseCase(hasLocationPermission)
    }

    fun requestLocationPermission() {
        viewModelScope.launch {
            _eventFlow.emit(Event.RequestLocationPermissionCheck)
        }
    }

    fun cancelLocationCollectJob() {
        locationFlow?.cancel()
    }

    fun startLocationCollectJob() {
        setLocationFlowJob()
    }

    companion object {
        private const val SEARCH_SIZE = 15
        private const val EXPIRED_GIFTICON_LIST_MAX_SIZE = 7
    }
}
