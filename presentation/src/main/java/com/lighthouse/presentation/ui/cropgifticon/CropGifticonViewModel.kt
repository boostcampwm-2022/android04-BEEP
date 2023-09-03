package com.lighthouse.presentation.ui.cropgifticon

import android.graphics.Bitmap
import android.graphics.RectF
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.presentation.R
import com.lighthouse.presentation.ui.cropgifticon.event.CropGifticonEvent
import com.lighthouse.presentation.util.flow.MutableEventFlow
import com.lighthouse.presentation.util.flow.asEventFlow
import com.lighthouse.presentation.util.resource.UIText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CropGifticonViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _eventsFlow = MutableEventFlow<CropGifticonEvent>()
    val eventsFlow = _eventsFlow.asEventFlow()

    var cropInfo = CropGifticonParams.getCropInfo(savedStateHandle)
        private set

    val cropImageMode = CropGifticonParams.getCropImageMode(savedStateHandle)

    val enableAspectRatio = CropGifticonParams.getEnableAspectRatio(savedStateHandle)
    val aspectRatio = CropGifticonParams.getAspectRatio(savedStateHandle)

    fun cancelCropImage() {
        viewModelScope.launch {
            _eventsFlow.emit(CropGifticonEvent.PopupBackStack)
        }
    }

    fun requestCropImage() {
        viewModelScope.launch {
            _eventsFlow.emit(CropGifticonEvent.RequestCrop)
        }
    }

    fun onCropImage(croppedBitmap: Bitmap?, croppedRect: RectF) {
        viewModelScope.launch {
            if (croppedBitmap == null) {
                _eventsFlow.emit(CropGifticonEvent.ShowSnackBar(UIText.StringResource(R.string.crop_gifticon_failed)))
            } else {
                _eventsFlow.emit(CropGifticonEvent.CompleteCrop(croppedBitmap, croppedRect))
            }
        }
    }

    fun onChangeCropRect(cropRect: RectF) {
        cropInfo = cropInfo.copy(croppedRect = cropRect)
    }
}
