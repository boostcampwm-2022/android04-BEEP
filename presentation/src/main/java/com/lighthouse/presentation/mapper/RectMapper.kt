package com.lighthouse.presentation.mapper

import android.graphics.Rect
import com.lighthouse.domain.model.Rectangle

fun Rect.toDomain(): Rectangle {
    return Rectangle(left, top, right, bottom)
}
