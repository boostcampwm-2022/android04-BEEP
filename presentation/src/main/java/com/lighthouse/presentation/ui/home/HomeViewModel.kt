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
import com.lighthouse.presentation.mapper.toPresentation
import com.lighthouse.presentation.mapper.toUiModel
import com.lighthouse.presentation.model.BrandPlaceInfoUiModel
import com.lighthouse.presentation.model.GifticonUiModel
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
                    .groupBy { it.brand }
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

    val expiredGifticon = allGifticons.transform { gifticons ->
        if (gifticons.isEmpty()) return@transform
        val gifticonFlatten = gifticons.values.flatten()
        val gifticonSize =
            if (gifticonFlatten.size < EXPIRED_GIFTICON_LIST_MAX_SIZE) gifticonFlatten.size else EXPIRED_GIFTICON_LIST_MAX_SIZE
        emit(gifticonFlatten.slice(0 until gifticonSize))
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _nearGifticon: MutableStateFlow<UiState<List<GifticonUiModel>>> = MutableStateFlow(UiState.Loading)
    val nearGifticon = _nearGifticon.asStateFlow()

    var nearBrandsInfo = listOf<BrandPlaceInfoUiModel>()
        private set

    private lateinit var recentLocation: VertexLocation

    init {
        viewModelScope.launch {
            getUserLocation().collect { location ->
                recentLocation = location
                getNearBrands(location.longitude, location.latitude)
            }
        }
    }

    private fun getNearBrands(x: Double, y: Double) {
        viewModelScope.launch {
            _nearGifticon.emit(UiState.Loading)
            runCatching { getBrandPlaceInfosUseCase(allBrands.value, x, y, SEARCH_SIZE) }
                .mapCatching { brand -> brand.toPresentation() }
                .onSuccess { brands ->
                    nearBrandsInfo = brands.sortedBy { diffLocation(it, recentLocation) }
                    val nearGifticon = nearBrandsInfo.distinctBy { it.brand }.mapNotNull { placeInfo ->
                        allGifticons.value[placeInfo.brand]?.first()?.toUiModel(diffLocation(placeInfo, recentLocation))
                    }.sortedBy { it.distance }
                    _nearGifticon.value = UiState.Success(nearGifticon)
                }
                .onFailure { throwable ->
                    _nearGifticon.value = when (throwable) {
                        BeepError.NetworkFailure -> UiState.NetworkFailure
                        else -> UiState.Failure
                    }
                }
        }
    }

    private fun diffLocation(
        location: BrandPlaceInfoUiModel,
        currentLocation: VertexLocation
    ) = LocationConverter.locationDistance(
        location.x.toDouble(),
        location.y.toDouble(),
        currentLocation.longitude,
        currentLocation.latitude
    )

    companion object {
        private const val SEARCH_SIZE = 15
        private const val EXPIRED_GIFTICON_LIST_MAX_SIZE = 7
    }
}
