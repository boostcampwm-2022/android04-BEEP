package com.lighthouse.presentation.ui.cropgifticon.event

import android.graphics.Bitmap
import android.graphics.RectF
import com.lighthouse.presentation.util.resource.UIText

sealed class CropGifticonEvent {

    object PopupBackStack : CropGifticonEvent()
    object RequestCrop : CropGifticonEvent()
    data class CompleteCrop(val croppedBitmap: Bitmap, val croppedRect: RectF) : CropGifticonEvent()
    data class ShowSnackBar(val uiText: UIText) : CropGifticonEvent()
}
