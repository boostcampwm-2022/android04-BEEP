package com.lighthouse.presentation.ui.map

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.lighthouse.domain.usecase.GetBrandPlaceInfosUseCase
import com.lighthouse.domain.usecase.GetGifticonsUseCase
import com.lighthouse.domain.usecase.GetUserLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    getGifticonsUseCase: GetGifticonsUseCase,
    savedStateHandle: SavedStateHandle,
    private val getBrandPlaceInfos: GetBrandPlaceInfosUseCase,
    private val getUserLocation: GetUserLocationUseCase
) : ViewModel()
