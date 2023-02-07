package com.lighthouse.presentation.mapper

import android.graphics.Rect
import com.lighthouse.beep.model.etc.Rectangle

fun Rect.toDomain(): Rectangle {
    return Rectangle(left, top, right, bottom)
}
