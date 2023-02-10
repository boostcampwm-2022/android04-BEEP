package com.lighthouse.presentation.ui.gallery

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.lighthouse.core.android.utils.resource.UIText
import com.lighthouse.core.utils.flow.MutableEventFlow
import com.lighthouse.core.utils.flow.asEventFlow
import com.lighthouse.domain.usecase.GetGalleryImagesUseCase
import com.lighthouse.presentation.R
import com.lighthouse.presentation.extra.Extras
import com.lighthouse.presentation.mapper.toPresentation
import com.lighthouse.presentation.model.GalleryUIModel
import com.lighthouse.presentation.ui.gallery.event.GalleryEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    getGalleryImagesUseCase: GetGalleryImagesUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _eventsFlow = MutableEventFlow<GalleryEvent>()
    val eventsFlow = _eventsFlow.asEventFlow()

    private val _pagingData = getGalleryImagesUseCase().cachedIn(viewModelScope)

    private val _selectedList = MutableStateFlow<List<GalleryUIModel.Gallery>>(
        savedStateHandle[Extras.KEY_SELECTED_GALLERY_ITEM] ?: emptyList()
    )
    val selectedList = _selectedList.asStateFlow()

    val list = _pagingData.combine(_selectedList) { pagingData, selectedList ->
        pagingData.map { galleryImage ->
            galleryImage.toPresentation(
                selectedList.indexOfFirst { gallery -> galleryImage.id == gallery.id }
            )
        }.insertSeparators { before: GalleryUIModel.Gallery?, after: GalleryUIModel.Gallery? ->
            if (before == null && after != null) {
                GalleryUIModel.Header(after.createdDate)
            } else if (before != null && after != null) {
                if (before.createdDate != after.createdDate) {
                    GalleryUIModel.Header(after.createdDate)
                } else {
                    null
                }
            } else {
                null
            }
        }
    }.cachedIn(viewModelScope)
        .stateIn(viewModelScope, SharingStarted.Eagerly, PagingData.empty())

    val isSelected = _selectedList.map { list ->
        list.isNotEmpty()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val titleText = _selectedList.map { list ->
        if (list.isNotEmpty()) {
            UIText.StringResource(R.string.gallery_selected, list.size)
        } else {
            UIText.StringResource(R.string.gallery_title)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UIText.Empty)

    fun selectItem(gallery: GalleryUIModel.Gallery) {
        val oldList = _selectedList.value
        val index = oldList.indexOfFirst { item ->
            item.id == gallery.id
        }
        _selectedList.value = if (index == -1) {
            oldList + listOf(gallery)
        } else {
            oldList.subList(0, index) + oldList.subList(index + 1, oldList.size)
        }
    }

    fun removeItem(gallery: GalleryUIModel.Gallery) {
        val oldList = _selectedList.value
        val index = oldList.indexOfFirst { item ->
            item.id == gallery.id
        }
        if (index != -1) {
            _selectedList.value =
                oldList.subList(0, index) + oldList.subList(index + 1, oldList.size)
        }
    }

    fun cancelPhotoSelection() {
        viewModelScope.launch {
            _eventsFlow.emit(GalleryEvent.PopupBackStack)
        }
    }

    fun completePhotoSelection() {
        viewModelScope.launch {
            _eventsFlow.emit(GalleryEvent.CompleteSelect)
        }
    }
}
