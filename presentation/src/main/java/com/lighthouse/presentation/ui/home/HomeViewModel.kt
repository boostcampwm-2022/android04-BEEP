package com.lighthouse.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.LocationConverter.diffLocation
import com.lighthouse.domain.LocationConverter.setDmsLocation
import com.lighthouse.domain.VertexLocation
import com.lighthouse.domain.model.BeepError
import com.lighthouse.domain.model.DbResult
import com.lighthouse.domain.usecase.GetBrandPlaceInfosUseCase
import com.lighthouse.domain.usecase.GetGifticonsUseCase
import com.lighthouse.domain.usecase.GetUserLocationUseCase
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getGifticonUseCase: GetGifticonsUseCase,
    private val getUserLocationUseCase: GetUserLocationUseCase,
    private val getBrandPlaceInfosUseCase: GetBrandPlaceInfosUseCase
) : ViewModel() {

    private var locationFlow: Job? = null

    private val _eventFlow = MutableEventFlow<HomeEvent>()
    val eventFlow = _eventFlow.asEventFlow()

    private val gifticons =
        getGifticonUseCase.getUsableGifticons().stateIn(viewModelScope, SharingStarted.Eagerly, DbResult.Loading)

    private val allBrands = gifticons.transform { gifticons ->
        if (gifticons is DbResult.Success) {
            emit(gifticons.data.map { it.brandLowerName }.distinct())
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val gifticonsMap = gifticons.transform { gifticons ->
        if (gifticons is DbResult.Success) {
            val gifticonGroup = gifticons.data.groupBy { it.brandLowerName }
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

    private val _uiState: MutableEventFlow<UiState<Unit>> = MutableEventFlow()
    val uiState = _uiState.asEventFlow()

    private var nearBrandsInfo = listOf<BrandPlaceInfoUiModel>()

    val isShimmer = MutableStateFlow(false)

    private val _nearGifticons: MutableStateFlow<List<GifticonUiModel>?> = MutableStateFlow(null)
    val nearGifticons = _nearGifticons.asStateFlow()

    val isEmptyNearBrands = nearGifticons.transform {
        val data = nearGifticons.value ?: kotlin.run {
            emit(false)
            return@transform
        }
        emit(data.isEmpty())
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private var prevVertex = MutableStateFlow<VertexLocation?>(null)

    var hasLocationPermission = MutableStateFlow(false)
        private set

    init {
        viewModelScope.launch {
            hasLocationPermission.collectLatest {
                if (it) observeLocationFlow()
            }
        }
    }

    private fun observeLocationFlow() {
        if (locationFlow?.isActive == true) return
        isShimmer.value = true

        viewModelScope.launch {
            combineLocationGifticon()
        }

        locationFlow = viewModelScope.launch {
            getUserLocationUseCase().collectLatest { location ->
                _uiState.emit(UiState.Loading)
                val currentDms = setDmsLocation(location)
                val prevDms = prevVertex.value?.let { setDmsLocation(it) }
                if (prevDms != currentDms) {
                    isShimmer.value = true
                    prevVertex.value = location
                }
            }
        }
    }

    private suspend fun combineLocationGifticon() {
        prevVertex.combine(gifticons) { location, _ ->
            location
        }.collectLatest { location ->
            location ?: return@collectLatest
            val x = location.longitude
            val y = location.latitude

            getBrandPlaceInfosUseCase(allBrands.value, x, y, SEARCH_SIZE)
                .mapCatching { brand -> brand.toPresentation() }
                .onSuccess { brands ->
                    nearBrandsInfo = brands
                    _nearGifticons.value = nearBrandsInfo
                        .distinctBy { it.brandLowerName }
                        .mapNotNull { placeInfo ->
                            gifticonsMap.value[placeInfo.brandLowerName]
                                ?.first()
                                ?.toPresentation(diffLocation(placeInfo.x, placeInfo.y, x, y))
                        }
                    when (_nearGifticons.value.isNullOrEmpty()) {
                        true -> _uiState.emit(UiState.NotFoundResults)
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

    fun gotoMap() {
        viewModelScope.launch {
            _eventFlow.emit(HomeEvent.NavigateMap(gifticonsMap.value.values.flatten(), nearBrandsInfo))
        }
    }

    fun requestLocationPermission() {
        viewModelScope.launch {
            _eventFlow.emit(HomeEvent.RequestLocationPermissionCheck)
        }
    }

    fun cancelLocationCollectJob() {
        locationFlow?.cancel()
    }

    fun updateLocationPermission(isLocationPermission: Boolean) {
        hasLocationPermission.value = isLocationPermission
    }

    companion object {
        private const val SEARCH_SIZE = 15
        private const val EXPIRED_GIFTICON_LIST_MAX_SIZE = 7
    }
}
