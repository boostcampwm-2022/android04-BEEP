package com.lighthouse.presentation.util

import com.lighthouse.domain.util.calcDday
import java.util.Calendar
import java.util.Date

object TimeCalculator {

    private const val EXPIRED_DAY = -1
    const val MAX_DAY = 365
    const val MIN_DAY = 0

    fun formatDdayToInt(endTime: Long): Int {
        val diffDate = Date(endTime).calcDday()
        return when {
            diffDate in MIN_DAY until MAX_DAY -> diffDate
            diffDate >= MAX_DAY -> MAX_DAY
            else -> EXPIRED_DAY
        }
    }

    fun calculateWorkerInitialDelay(): Long {
        val now = Calendar.getInstance().time
        val cal = Calendar.getInstance().apply {
            add(Calendar.DATE, 1)
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.time

        val diff = cal.time - now.time
        return diff / (60 * 60 * 1000L) % 24
    }
}
