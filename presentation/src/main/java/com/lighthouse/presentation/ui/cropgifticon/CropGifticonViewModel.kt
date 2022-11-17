package com.lighthouse.presentation.ui.cropgifticon

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.presentation.extra.Extras
import com.lighthouse.presentation.ui.cropgifticon.event.CropGifticonEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CropGifticonViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _eventsFlow = MutableSharedFlow<CropGifticonEvents>()
    val eventsFlow: SharedFlow<CropGifticonEvents> = _eventsFlow

    val isComplete = MutableStateFlow(true)

    val originUri = savedStateHandle.get<Uri>(Extras.OriginImage)

    fun cancelCropImage() {
        viewModelScope.launch {
            _eventsFlow.emit(CropGifticonEvents.CANCEL)
        }
    }

    fun completeCropImage() {
        viewModelScope.launch {
            _eventsFlow.emit(CropGifticonEvents.COMPLETE)
        }
    }
}
