package com.lighthouse.presentation.binding

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.lighthouse.presentation.R
import java.text.DecimalFormat
import java.util.Date

@BindingAdapter("dateFormat")
fun applyDateFormat(view: TextView, date: Date) {
    view.text = view.context.getString(R.string.all_date, date.year, date.month, date.day) // TODO Calendar 로 변경?
}

@BindingAdapter("concurrencyFormat")
fun applyConcurrencyFormat(view: TextView, amount: Int) {
    val format = view.context.resources.getString(R.string.all_concurrency_format)
    val formattedNumber = DecimalFormat(format).format(amount)
    view.setText(if (amount > 0) view.context.resources.getString(R.string.all_cash_unit, formattedNumber) else "")
}
