package com.lighthouse.presentation.ui.cropgifticon.view

import android.graphics.Bitmap
import android.graphics.RectF

interface OnCropImageListener {
    fun onCrop(croppedBitmap: Bitmap?, croppedRect: RectF?)
}
