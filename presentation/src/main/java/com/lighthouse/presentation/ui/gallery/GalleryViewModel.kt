package com.lighthouse.presentation.ui.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.usecase.GetGalleryImagesUseCase
import com.lighthouse.presentation.mapper.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val getGalleryImagesUseCase: GetGalleryImagesUseCase
) : ViewModel() {

    val list = flow {
        val images = getGalleryImagesUseCase().map {
            it.toPresentation()
        }
        emit(images)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
}
