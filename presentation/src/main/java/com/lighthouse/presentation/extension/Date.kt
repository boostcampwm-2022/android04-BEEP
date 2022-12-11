package com.lighthouse.presentation.extension

import android.content.Context
import com.lighthouse.domain.util.isExpired
import com.lighthouse.presentation.R
import com.lighthouse.presentation.util.TimeCalculator
import com.lighthouse.presentation.util.TimeCalculator.MAX_DAY
import com.lighthouse.presentation.util.TimeCalculator.MIN_DAY
import com.lighthouse.presentation.util.resource.UIText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Date.toYear(): Int {
    return SimpleDateFormat("yyyy", Locale.getDefault()).format(this).toInt()
}

fun Date.toMonth(): Int {
    return SimpleDateFormat("M", Locale.getDefault()).format(this).toInt()
}

fun Date.toDayOfMonth(): Int {
    return SimpleDateFormat("d", Locale.getDefault()).format(this).toInt()
}

fun Date.toDday(context: Context): String {
    val dDay = TimeCalculator.formatDdayToInt(time)
    return when {
        dDay == MIN_DAY -> context.getString(R.string.all_d_very_day)
        isExpired() -> context.getString(R.string.all_d_day_expired)
        dDay in MIN_DAY + 1 until MAX_DAY -> String.format(
            context.getString(R.string.all_d_day),
            dDay
        )
        else -> context.getString(R.string.all_d_day_more_than_year)
    }
}

fun Date.toExpireDate(context: Context): String {
    return UIText.StringResource(
        R.string.all_expired_date,
        toYear(),
        toMonth(),
        toDayOfMonth()
    ).asString(context)
}

fun Date.toString(pattern: String): String {
    val format = SimpleDateFormat(pattern, Locale.getDefault())
    return format.format(this)
}
