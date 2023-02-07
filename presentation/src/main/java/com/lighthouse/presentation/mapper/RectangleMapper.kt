package com.lighthouse.presentation.mapper

import android.graphics.Rect
import com.lighthouse.beep.model.etc.Rectangle

fun Rectangle.toPresentation(): Rect {
    return Rect(left, top, right, bottom)
}
