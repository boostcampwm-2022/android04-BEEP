package com.lighthouse.utils.recognizer.processor

import android.content.res.Resources
import android.graphics.Bitmap

internal open class ScaleProcessor {
    private val screenWidth by lazy {
        Resources.getSystem().displayMetrics.widthPixels
    }
    private val screenHeight by lazy {
        Resources.getSystem().displayMetrics.heightPixels
    }
    private val screenAspect by lazy {
        screenWidth.toFloat() / screenHeight
    }

    fun scaleProcess(bitmap: Bitmap): Bitmap {
        val originWidth = bitmap.width
        val originHeight = bitmap.height
        val originAspect = originWidth.toFloat() / originHeight

        return if (originAspect > screenAspect) {
            Bitmap.createScaledBitmap(bitmap, screenWidth, (screenWidth / originAspect).toInt(), false)
        } else {
            Bitmap.createScaledBitmap(bitmap, (screenHeight * originAspect).toInt(), screenHeight, false)
        }
    }
}
