package com.lighthouse.presentation.ui.cropgifticon

import android.graphics.Bitmap
import android.graphics.RectF
import com.lighthouse.presentation.util.resource.UIText

sealed class CropGifticonEvents {

    object PopupBackStack : CropGifticonEvents()
    object RequestCrop : CropGifticonEvents()
    data class CompleteCrop(val croppedBitmap: Bitmap, val croppedRect: RectF) : CropGifticonEvents()
    data class ShowSnackBar(val uiText: UIText) : CropGifticonEvents()
}
