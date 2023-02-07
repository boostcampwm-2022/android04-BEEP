package com.lighthouse.common.mapper

import android.graphics.Rect
import android.graphics.RectF
import com.lighthouse.beep.model.etc.Rectangle

fun Rectangle.toRect(): Rect {
    return Rect(left, top, right, bottom)
}

fun Rectangle.toRectF(): RectF {
    return RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
}

fun Rect.toDomain(): Rectangle {
    return Rectangle(left, top, right, bottom)
}

fun RectF.toDomain(): Rectangle {
    return Rectangle(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
}
