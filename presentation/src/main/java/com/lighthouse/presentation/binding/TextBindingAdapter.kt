package com.lighthouse.presentation.binding

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.lighthouse.presentation.R
import com.lighthouse.presentation.util.TimeCalculator
import com.lighthouse.presentation.util.TimeCalculator.MAX_DAY
import com.lighthouse.presentation.util.TimeCalculator.MIN_DAY
import com.lighthouse.presentation.utils.extention.toDate
import com.lighthouse.presentation.utils.extention.toMonth
import com.lighthouse.presentation.utils.extention.toYear
import java.util.Date

@BindingAdapter("setExportedDate")
fun setExportedDate(view: TextView, date: Date) {
    view.text = view.context.getString(R.string.all_expired_date, date.toYear(), date.toMonth(), date.toDate())
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
