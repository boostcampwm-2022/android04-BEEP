package com.lighthouse.presentation.ui.addgifticon

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.presentation.model.AddGifticonUIModel
import com.lighthouse.presentation.model.GalleryUIModel
import com.lighthouse.presentation.ui.addgifticon.event.AddGifticonDirections
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AddGifticonViewModel : ViewModel() {

    val directionsFlow = MutableSharedFlow<AddGifticonDirections>()

    private val _displayList = MutableStateFlow<List<AddGifticonUIModel>>(listOf(AddGifticonUIModel.Gallery))
    val displayList = _displayList.asStateFlow()

    private val currentPos = MutableStateFlow(-1)
    val currentGifticon = displayList.combineTransform(currentPos) { list, pos ->
        if (pos in list.indices && list[pos] is AddGifticonUIModel.Gifticon) {
            emit(list[pos] as AddGifticonUIModel.Gifticon)
        } else {
            emit(null)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private var originImage: List<GalleryUIModel.Gallery> = listOf()

    fun loadGalleryImages(list: List<GalleryUIModel.Gallery>) {
        originImage = list
        _displayList.value = listOf(AddGifticonUIModel.Gallery) + list.map {
            AddGifticonUIModel.Gifticon(it.id, it.uri, false, false)
        }
    }

    fun selectGifticon(position: Int) {
        viewModelScope.launch {
            currentPos.emit(position)
        }
    }

    fun croppedImage(uri: Uri) {
    }

    fun deleteGifticon(position: Int) {
    }

    fun popBackstack() {
        viewModelScope.launch {
            directionsFlow.emit(AddGifticonDirections.Back)
        }
    }

    fun gotoGallery() {
        viewModelScope.launch {
            directionsFlow.emit(AddGifticonDirections.Gallery(originImage))
        }
    }

    fun gotoCropGifticon() {
        val originUri = currentGifticon.value?.origin ?: return
        viewModelScope.launch {
            directionsFlow.emit(AddGifticonDirections.CropGifticon(originUri))
        }
    }

    fun gotoOriginGifticon() {
        val originUri = currentGifticon.value?.origin ?: return
        viewModelScope.launch {
            directionsFlow.emit(AddGifticonDirections.OriginGifticon(originUri))
        }
    }
}
