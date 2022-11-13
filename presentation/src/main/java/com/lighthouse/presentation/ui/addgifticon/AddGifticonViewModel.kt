package com.lighthouse.presentation.ui.addgifticon

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.presentation.model.AddGifticonUIModel
import com.lighthouse.presentation.ui.addgifticon.event.AddGifticonDirections
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AddGifticonViewModel : ViewModel() {
    val directionsFlow = MutableSharedFlow<AddGifticonDirections>()

    private val _displayList = MutableStateFlow<List<AddGifticonUIModel>>(listOf(AddGifticonUIModel.Gallery))
    val displayList: StateFlow<List<AddGifticonUIModel>> = _displayList

    private val currentPos = MutableStateFlow(-1)
    private val currentGifticon = displayList.combineTransform(currentPos) { list, pos ->
        if (pos in list.indices && list[pos] is AddGifticonUIModel.Gifticon) {
            emit(list[pos] as AddGifticonUIModel.Gifticon)
        } else {
            emit(null)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun selectGifticon(position: Int) {
        viewModelScope.launch {
            currentPos.emit(position)
        }
    }

    fun popBackstack() {
        viewModelScope.launch {
            directionsFlow.emit(AddGifticonDirections.Back)
        }
    }

    fun gotoGallery() {
        val list = displayList.value.mapNotNull {
            (it as? AddGifticonUIModel.Gifticon)?.uri
        }

        viewModelScope.launch {
            directionsFlow.emit(AddGifticonDirections.Gallery(list))
        }
    }

    fun gotoCropGifticon() {
        val originUri = currentGifticon.value?.uri ?: return
        viewModelScope.launch {
            directionsFlow.emit(AddGifticonDirections.CropGifticon(originUri))
        }
    }

    fun gotoOriginGifticon() {
        val originUri = currentGifticon.value?.uri ?: return
        viewModelScope.launch {
            directionsFlow.emit(AddGifticonDirections.OriginGifticon(originUri))
        }
    }
}
