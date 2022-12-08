package com.lighthouse.mapper

import android.graphics.Rect
import com.lighthouse.domain.model.Rectangle

fun Rectangle.toEntity(): Rect {
    return Rect(left, top, right, bottom)
}
