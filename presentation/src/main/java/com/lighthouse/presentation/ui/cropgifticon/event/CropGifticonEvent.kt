package com.lighthouse.presentation.ui.cropgifticon.event

import android.graphics.Bitmap

sealed class CropGifticonEvent {

    object Cancel : CropGifticonEvent()

    object Crop : CropGifticonEvent()

    data class Complete(val bitmap: Bitmap) : CropGifticonEvent()
}
