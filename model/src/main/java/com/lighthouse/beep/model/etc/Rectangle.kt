package com.lighthouse.beep.model.etc

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

    fun sampling(sampleSize: Int?): Rectangle {
        val size = sampleSize ?: 1
        return Rectangle(
            left / size,
            top / size,
            right / size,
            bottom / size
        )
    }
}
