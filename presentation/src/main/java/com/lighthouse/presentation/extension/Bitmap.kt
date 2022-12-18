package com.lighthouse.presentation.extension

import android.graphics.Bitmap
import android.graphics.Matrix

fun Bitmap.rotated(degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, false)
}
