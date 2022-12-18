package com.lighthouse.presentation.mapper

import android.graphics.Rect
import com.lighthouse.domain.model.Rectangle

fun Rectangle.toPresentation(): Rect {
    return Rect(left, top, right, bottom)
}
