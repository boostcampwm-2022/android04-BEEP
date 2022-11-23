package com.lighthouse.presentation.ui.cropgifticon.event

import android.graphics.Bitmap

sealed class CropGifticonEvents {

    object Cancel : CropGifticonEvents()

    object Crop : CropGifticonEvents()

    data class Complete(val bitmap: Bitmap) : CropGifticonEvents()
}
