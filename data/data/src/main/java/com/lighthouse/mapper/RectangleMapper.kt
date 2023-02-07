package com.lighthouse.mapper

import android.graphics.Rect
import com.lighthouse.beep.model.etc.Rectangle

fun Rectangle.toEntity(): Rect {
    return Rect(left, top, right, bottom)
}
