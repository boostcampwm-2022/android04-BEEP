package com.lighthouse.presentation.ui.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.lighthouse.domain.usecase.GetGalleryImagesUseCase
import com.lighthouse.presentation.R
import com.lighthouse.presentation.mapper.toPresentation
import com.lighthouse.presentation.model.GalleryUIModel
import com.lighthouse.presentation.ui.gallery.event.GalleryEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    getGalleryImagesUseCase: GetGalleryImagesUseCase
) : ViewModel() {

    private val _eventsFlow = MutableSharedFlow<GalleryEvents>()
    val eventsFlow: SharedFlow<GalleryEvents> = _eventsFlow

    private val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val list = getGalleryImagesUseCase().map { pagingData ->
        pagingData.map {
            it.toPresentation()
        }.insertSeparators { before: GalleryUIModel.Gallery?, after: GalleryUIModel.Gallery? ->
            if (before == null && after != null) {
                GalleryUIModel.Header(format.format(after.date))
            } else if (before != null && after != null) {
                val beforeDate = format.format(before.date)
                val afterDate = format.format(after.date)
                if (beforeDate != afterDate) {
                    GalleryUIModel.Header(afterDate)
                } else {
                    null
                }
            } else {
                null
            }
        }
    }.cachedIn(viewModelScope)

    private val _selectedSize = MutableStateFlow(0)

    val selectedSize = _selectedSize.map { size ->
        if (size == 0) "" else "$size"
    }.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val selectedSizePostFix = _selectedSize.map { size ->
        if (size == 0) R.string.gallery_title else R.string.gallery_selected
    }.stateIn(viewModelScope, SharingStarted.Eagerly, R.string.gallery_title)

    fun selectItem(size: Int) {
        _selectedSize.value = size
    }

    fun cancelPhotoSelection() {
        viewModelScope.launch {
            _eventsFlow.emit(GalleryEvents.CANCEL)
        }
    }

    fun completePhotoSelection() {
        viewModelScope.launch {
            _eventsFlow.emit(GalleryEvents.COMPLETE)
        }
    }
}
