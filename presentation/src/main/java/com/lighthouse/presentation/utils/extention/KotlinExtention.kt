package com.lighthouse.presentation.utils.extention

import android.content.res.Resources
import android.util.TypedValue

val Int.dp
    get() = Resources.getSystem().displayMetrics?.let { dm ->
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), dm)
    } ?: 0f
