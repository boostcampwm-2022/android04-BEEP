package com.lighthouse.presentation.ui.common.dialog

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import android.widget.NumberPicker
import com.lighthouse.presentation.R
import com.lighthouse.presentation.extension.toDate
import com.lighthouse.presentation.extension.toMonth
import com.lighthouse.presentation.extension.toYear
import java.util.Date

class SpinnerDatePickerDialog(context: Context, initialDate: Date = Date(), listener: OnDateSetListener) :
    DatePickerDialog(
        context,
        R.style.Theme_BEEP_DatePicker,
        listener,
        initialDate.toYear(),
        initialDate.toMonth(),
        initialDate.toDate()
    ) {

    private val monthNumbers = Array(12) { String.format("%02d", it + 1) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setMonthNumberPicker()
    }

    override fun onDateChanged(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        super.onDateChanged(view, year, month, dayOfMonth)
        setMonthNumberPicker()
    }

    private fun setMonthNumberPicker() {
        val month = context.resources.getIdentifier("android:id/month", null, null)
        findViewById<NumberPicker>(month)?.apply {
            displayedValues = monthNumbers
        }
    }
}
