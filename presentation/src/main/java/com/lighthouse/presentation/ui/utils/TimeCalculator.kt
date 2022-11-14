package com.lighthouse.presentation.ui.utils

object TimeCalculator {

    const val MAX_DAY = 365
    const val MIN_DAY = 0

    fun formatDdayToInt(endTime: Long): Int {
        val today = System.currentTimeMillis()
        val diffDate = (endTime - today) / (24 * 60 * 60 * 1000)

        return if (diffDate < MAX_DAY || diffDate > MIN_DAY) {
            diffDate.toInt()
        } else {
            MAX_DAY
        }
    }
}
