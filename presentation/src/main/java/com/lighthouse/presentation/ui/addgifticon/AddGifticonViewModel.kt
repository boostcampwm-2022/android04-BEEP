package com.lighthouse.presentation.ui.addgifticon

import android.graphics.RectF
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.presentation.R
import com.lighthouse.presentation.mapper.toAddGifticonModel
import com.lighthouse.presentation.model.AddGifticonUIModel
import com.lighthouse.presentation.model.GalleryUIModel
import com.lighthouse.presentation.util.flow.MutableEventFlow
import com.lighthouse.presentation.util.flow.asEventFlow
import com.lighthouse.presentation.util.resource.UIText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AddGifticonViewModel : ViewModel() {

    private val _eventFlow = MutableEventFlow<AddGifticonEvents>()
    val eventFlow = _eventFlow.asEventFlow()

    private val _displayList = MutableStateFlow<List<AddGifticonUIModel>>(listOf(AddGifticonUIModel.Gallery))
    val displayList = _displayList.asStateFlow()

    private var _selectedId = MutableStateFlow(-1L)
    val selectedId = _selectedId.asStateFlow()

    val selectedGifticon = _selectedId.combine(_displayList) { id, list ->
        list.find {
            it is AddGifticonUIModel.Gifticon && it.id == id
        } as? AddGifticonUIModel.Gifticon
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val _registeredSizeText = MutableStateFlow(UIText.StringResource(R.string.add_gifticon_empty))
    val registeredSizeText = _registeredSizeText.asStateFlow()

    fun loadGalleryImages(list: List<GalleryUIModel.Gallery>) {
        viewModelScope.launch {
            _registeredSizeText.value = if (list.isEmpty()) {
                UIText.StringResource(R.string.add_gifticon_empty)
            } else {
                UIText.StringResource(R.string.add_gifticon_registered, list.size)
            }

            val oldList = _displayList.value
            _displayList.value = listOf(AddGifticonUIModel.Gallery) + list.map { newItem ->
                oldList.find { oldItem ->
                    oldItem is AddGifticonUIModel.Gifticon && newItem.id == oldItem.id
                } ?: newItem.toAddGifticonModel()
            }
        }
    }

    private fun updateSelectedGifticon(update: (AddGifticonUIModel.Gifticon) -> AddGifticonUIModel.Gifticon) {
        val index = _displayList.value.indexOfFirst {
            it is AddGifticonUIModel.Gifticon && it.id == selectedId.value
        }
        if (index == -1) {
            return
        }
        val oldList = _displayList.value
        val oldItem = oldList[index]
        if (oldItem !is AddGifticonUIModel.Gifticon) {
            return
        }
        _displayList.value =
            oldList.subList(0, index) + listOf(update(oldItem)) + oldList.subList(index + 1, oldList.size)
    }

    fun croppedImage(croppedUri: Uri, croppedRect: RectF) {
        updateSelectedGifticon {
            it.copy(cropped = croppedUri, cropRect = croppedRect)
        }
    }

    fun selectGifticon(gifticon: AddGifticonUIModel.Gifticon) {
        _selectedId.value = gifticon.id
    }

    fun deleteGifticon(gifticon: AddGifticonUIModel.Gifticon) {
        val index = _displayList.value.indexOfFirst {
            it is AddGifticonUIModel.Gifticon && it.id == gifticon.id
        }
        if (index == -1) {
            return
        }
        val oldList = _displayList.value
        val deleteItem = oldList[index]
        if (deleteItem !is AddGifticonUIModel.Gifticon) {
            return
        }
        if (selectedId.value == deleteItem.id) {
            _selectedId.value = -1
        }
        _displayList.value =
            oldList.subList(0, index) + oldList.subList(index + 1, oldList.size)
    }

    fun popBackstack() {
        viewModelScope.launch {
            _eventFlow.emit(AddGifticonEvents.PopupBackStack)
        }
    }

    fun gotoGallery() {
        val list = _displayList.value.filterIsInstance<AddGifticonUIModel.Gifticon>().map { it.id }
        viewModelScope.launch {
            _eventFlow.emit(AddGifticonEvents.NavigateToGallery(list))
        }
    }

    fun gotoCropGifticon() {
        val gifticon = selectedGifticon.value ?: return
        viewModelScope.launch {
            _eventFlow.emit(
                AddGifticonEvents.NavigateToCropGifticon(
                    gifticon.origin,
                    gifticon.cropRect
                )
            )
        }
    }

    fun gotoOriginGifticon() {
        val originUri = selectedGifticon.value?.origin ?: return
        viewModelScope.launch {
            _eventFlow.emit(AddGifticonEvents.ShowOriginGifticon(originUri))
        }
    }
}
