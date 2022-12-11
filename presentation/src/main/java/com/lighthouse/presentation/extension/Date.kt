package com.lighthouse.presentation.extension

import android.content.Context
import com.lighthouse.domain.util.isExpired
import com.lighthouse.presentation.R
import com.lighthouse.presentation.util.TimeCalculator
import com.lighthouse.presentation.util.resource.UIText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Date.toYear(): Int {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return calendar.get(Calendar.YEAR)
}

fun Date.toMonth(): Int {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return calendar.get(Calendar.MONTH) + 1
}

fun Date.toDate(): Int {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return calendar.get(Calendar.DATE)
}

fun Date.toDday(context: Context): String {
    val dDay = TimeCalculator.formatDdayToInt(time)
    return when {
        dDay in TimeCalculator.MIN_DAY until TimeCalculator.MAX_DAY -> String.format(
            context.getString(R.string.all_d_day),
            dDay
        )
        isExpired() -> context.getString(R.string.all_d_day_expired)
        else -> context.getString(R.string.all_d_day_more_than_year)
    }
}

fun Date.toExpireDate(context: Context): String {
    return UIText.StringResource(
        R.string.all_expired_date,
        toYear(),
        toMonth(),
        toDate()
    ).asString(context)
}

fun Date.toString(pattern: String): String {
    val format = SimpleDateFormat(pattern, Locale.getDefault())
    return format.format(this)
}
