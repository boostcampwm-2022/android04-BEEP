package com.lighthouse.presentation.extension

import android.view.View
import com.lighthouse.core.android.exts.screenHeight
import com.lighthouse.core.android.exts.screenWidth

fun View.isOnScreen(): Boolean {
    val viewLocation = IntArray(2)
    getLocationOnScreen(viewLocation)

    return viewLocation[0] in 0..screenWidth && viewLocation[1] in 0..screenHeight
}
