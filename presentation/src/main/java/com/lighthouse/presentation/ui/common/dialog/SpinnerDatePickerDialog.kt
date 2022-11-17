package com.lighthouse.presentation.ui.common.dialog

import android.app.DatePickerDialog
import android.content.Context
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
    init {
        datePicker.layoutMode = 1
    }
}
