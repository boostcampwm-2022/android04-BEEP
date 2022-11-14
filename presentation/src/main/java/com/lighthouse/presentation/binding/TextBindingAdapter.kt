package com.lighthouse.presentation.binding

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.lighthouse.presentation.R
import com.lighthouse.presentation.ui.utils.TimeCalculator
import com.lighthouse.presentation.ui.utils.TimeCalculator.MAX_DAY
import com.lighthouse.presentation.ui.utils.TimeCalculator.MIN_DAY
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@BindingAdapter("setExportedDate")
fun setExportedDate(view: TextView, date: Date) {
    view.text = date.toString("~ yyyy-MM-dd")
}

@BindingAdapter("setDday")
fun setDday(view: TextView, date: Date) {
    val dDay = TimeCalculator.formatDdayToInt(date.time)
    view.text = when {
        dDay in MIN_DAY until MAX_DAY -> String.format(view.context.getString(R.string.all_d_day), dDay)
        dDay < MIN_DAY -> view.context.getString(R.string.all_d_day_expired)
        else -> view.context.getString(R.string.all_d_day_more_than_year)
    }
}

private fun Date.toString(format: String): String {
    val dateFormatter = SimpleDateFormat(format, Locale.getDefault())
    return dateFormatter.format(this)
}
