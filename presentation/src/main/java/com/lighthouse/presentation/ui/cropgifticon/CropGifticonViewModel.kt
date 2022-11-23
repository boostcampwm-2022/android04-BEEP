package com.lighthouse.presentation.ui.cropgifticon

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.presentation.R
import com.lighthouse.presentation.extra.Extras
import com.lighthouse.presentation.ui.cropgifticon.event.CropGifticonEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CropGifticonViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _eventsFlow = MutableSharedFlow<CropGifticonEvents>()
    val eventsFlow = _eventsFlow.asSharedFlow()

    private val _messageFlow = MutableSharedFlow<Int>()
    val messageFlow = _messageFlow.asSharedFlow()

    val isComplete = MutableStateFlow(true)

    val originUri = savedStateHandle.get<Uri>(Extras.OriginImage)

    fun cancelCropImage() {
        viewModelScope.launch {
            _eventsFlow.emit(CropGifticonEvents.Cancel)
        }
    }

    fun requestCropImage() {
        viewModelScope.launch {
            _eventsFlow.emit(CropGifticonEvents.Crop)
        }
    }

    fun onCropImageListener(bitmap: Bitmap?) {
        viewModelScope.launch {
            if (bitmap == null) {
                _messageFlow.emit(R.string.crop_gifticon_failed)
            } else {
                _eventsFlow.emit(CropGifticonEvents.Complete(bitmap))
            }
        }
    }
}
