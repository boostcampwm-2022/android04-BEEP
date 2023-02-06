package com.lighthouse.core.utils.time

import com.lighthouse.core.exts.calcDday
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

    /**
     * 현재 시간에서 다음 날 지정한 시간까지의 차이 구하는 함수
     * @param date 현재 날짜에서 몇 일 이후에 할지
     * @param hour date 이후 몇 시로 할지 기준 값
     * @param minute date 이후 몇 분으로 할지 기준 값
     * @param second date 이후 몇 초로 할지 기준 값
     * @return 몇 시간 차이나는지 값
     */
    fun calculateAfterDateDiffHour(
        date: Int = 1,
        hour: Int = 9,
        minute: Int = 0,
        second: Int = 0
    ): Long {
        val now = Calendar.getInstance().time
        val cal = Calendar.getInstance().apply {
            add(Calendar.DATE, date)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, second)
        }.time

        val diff = cal.time - now.time
        return diff / (60 * 60 * 1000L) % 24
    }
}
