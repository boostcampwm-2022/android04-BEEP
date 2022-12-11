package com.lighthouse.presentation.util

import com.lighthouse.domain.util.calcDday
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
}
