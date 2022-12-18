package com.lighthouse.presentation.ui.cropgifticon

import android.graphics.Bitmap
import android.graphics.RectF
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.presentation.R
import com.lighthouse.presentation.extra.Extras
import com.lighthouse.presentation.ui.cropgifticon.event.CropGifticonEvent
import com.lighthouse.presentation.ui.cropgifticon.view.CropImageInfo
import com.lighthouse.presentation.util.flow.MutableEventFlow
import com.lighthouse.presentation.util.flow.asEventFlow
import com.lighthouse.presentation.util.resource.UIText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CropGifticonViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _eventsFlow = MutableEventFlow<CropGifticonEvent>()
    val eventsFlow = _eventsFlow.asEventFlow()

    var cropInfo = CropImageInfo(
        savedStateHandle.get<Uri>(Extras.KEY_ORIGIN_IMAGE),
        savedStateHandle.get<RectF>(Extras.KEY_CROPPED_RECT)
    )
        private set

    val enableAspectRatio = savedStateHandle.get<Boolean>(Extras.KEY_ENABLE_ASPECT_RATIO) ?: true
    val aspectRatio = savedStateHandle.get<Float>(Extras.KEY_ASPECT_RATIO) ?: 1f

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
