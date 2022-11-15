package com.lighthouse.presentation.extension

import android.content.res.Resources
import android.util.TypedValue

val Int.dp
    get() = Resources.getSystem().displayMetrics?.let { dm ->
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), dm)
    } ?: 0f

val screenWidth: Int
    get() = Resources.getSystem().displayMetrics?.widthPixels ?: 0

val screenHeight: Int
    get() = Resources.getSystem().displayMetrics?.heightPixels ?: 0
