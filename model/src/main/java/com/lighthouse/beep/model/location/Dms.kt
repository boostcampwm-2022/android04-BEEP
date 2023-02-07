package com.lighthouse.beep.model.location

/**
 * @property degree 도
 * @property minutes 분
 * @property seconds 초
 */
data class Dms(
    val degree: Int,
    val minutes: Int,
    val seconds: Int
) {
    fun dmsToString() = "${degree}${fillZero(minutes)}${fillZero(seconds)}"

    private fun fillZero(seconds: Int) = seconds.toString().padStart(2, '0')
}
