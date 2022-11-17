package com.lighthouse.presentation.binding

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.lighthouse.presentation.R
import com.lighthouse.presentation.extension.toDate
import com.lighthouse.presentation.extension.toMonth
import com.lighthouse.presentation.extension.toYear
import java.text.DecimalFormat
import java.util.Date

@BindingAdapter("dateFormat")
fun applyDateFormat(view: TextView, date: Date) {
    view.text = view.context.getString(R.string.all_date, date.toYear(), date.toMonth(), date.toDate())
}

@BindingAdapter("concurrencyFormat")
fun applyConcurrencyFormat(view: TextView, amount: Int) {
    val format = view.context.resources.getString(R.string.all_concurrency_format)
    val formattedNumber = DecimalFormat(format).format(amount)
    view.setText(if (amount > 0) view.context.resources.getString(R.string.all_cash_unit, formattedNumber) else "")
}
