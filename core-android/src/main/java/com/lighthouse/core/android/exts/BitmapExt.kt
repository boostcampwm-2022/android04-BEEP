package com.lighthouse.core.android.exts

import android.graphics.Bitmap
import android.graphics.Matrix

fun Bitmap.rotated(degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, false)
}

fun Bitmap.centerCrop(aspectRatio: Float): Bitmap {
    val bitmapAspectRatio = width.toFloat() / height
    return if (bitmapAspectRatio > aspectRatio) {
        val newWidth = (height * aspectRatio).toInt()
        Bitmap.createBitmap(this, (width - newWidth) / 2, 0, newWidth, height)
    } else {
        val newHeight = (width / aspectRatio).toInt()
        Bitmap.createBitmap(this, 0, (height - newHeight) / 2, width, newHeight)
    }
}
