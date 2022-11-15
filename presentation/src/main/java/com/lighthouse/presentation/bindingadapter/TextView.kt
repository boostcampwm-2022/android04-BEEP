package com.lighthouse.presentation.bindingadapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.lighthouse.presentation.R
import java.util.Date

@BindingAdapter("dateFormat")
fun applyDateFormat(view: TextView, date: Date) {
    view.text = view.context.getString(R.string.all_date, date.year, date.month, date.day) // TODO Calendar 로 변경?
}
