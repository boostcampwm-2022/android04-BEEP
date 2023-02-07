package com.lighthouse.presentation.extension

import android.content.Context
import android.text.Spannable
import com.lighthouse.core.android.utils.resource.UIText
import com.lighthouse.core.exts.isExpired
import com.lighthouse.core.exts.toDayOfMonth
import com.lighthouse.core.exts.toMonth
import com.lighthouse.core.exts.toYear
import com.lighthouse.core.utils.time.TimeCalculator
import com.lighthouse.core.utils.time.TimeCalculator.MAX_DAY
import com.lighthouse.core.utils.time.TimeCalculator.MIN_DAY
import com.lighthouse.presentation.R
import java.util.Date

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

fun Date.toExpireDate(context: Context): Spannable {
    return UIText.StringResource(
        R.string.all_expired_date,
        toYear(),
        toMonth(),
        toDayOfMonth()
    ).asString(context)
}
