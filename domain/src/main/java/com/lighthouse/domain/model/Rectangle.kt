package com.lighthouse.domain.model

data class Rectangle(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int
) {

    val width
        get() = right - left

    val height
        get() = bottom - top
}
