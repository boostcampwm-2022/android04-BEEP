package com.lighthouse.presentation.util

object TimeCalculator {

    private const val EXPIRED_DAY = -1
    const val MAX_DAY = 365
    const val MIN_DAY = 0

    fun formatDdayToInt(endTime: Long): Int {
        val today = System.currentTimeMillis()
        val diffDate = (endTime - today) / (24 * 60 * 60 * 1000)

        return if (diffDate in MIN_DAY until MAX_DAY) {
            diffDate.toInt()
        } else if (diffDate >= MAX_DAY) {
            MAX_DAY
        } else {
            EXPIRED_DAY
        }
    }
}
