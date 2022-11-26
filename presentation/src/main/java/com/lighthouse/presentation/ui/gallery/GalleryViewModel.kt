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
import com.lighthouse.presentation.util.flow.MutableEventFlow
import com.lighthouse.presentation.util.flow.asEventFlow
import com.lighthouse.presentation.util.resource.UIText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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

    private val _eventsFlow = MutableEventFlow<GalleryEvents>()
    val eventsFlow = _eventsFlow.asEventFlow()

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

    val isSelected = _selectedSize.map { size ->
        size > 0
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val titleText = _selectedSize.map { size ->
        if (size == 0) {
            UIText.StringResource(R.string.gallery_title)
        } else {
            UIText.StringResource(R.string.gallery_selected, size)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UIText.Empty)

    fun selectItem(size: Int) {
        _selectedSize.value = size
    }

    fun cancelPhotoSelection() {
        viewModelScope.launch {
            _eventsFlow.emit(GalleryEvents.PopupBackStack)
        }
    }

    fun completePhotoSelection() {
        viewModelScope.launch {
            _eventsFlow.emit(GalleryEvents.CompleteSelect)
        }
    }
}
