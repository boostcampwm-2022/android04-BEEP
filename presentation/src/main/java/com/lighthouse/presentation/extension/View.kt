package com.lighthouse.presentation.extension

import android.view.View

fun View.isOnScreen(): Boolean {
    val viewLocation = IntArray(2)
    getLocationOnScreen(viewLocation)
    val screenHeight = resources.displayMetrics.heightPixels
    val screenWidth = resources.displayMetrics.widthPixels

    return viewLocation[0] in 0..screenWidth && viewLocation[1] in 0..screenHeight
}
