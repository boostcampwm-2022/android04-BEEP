package com.lighthouse.presentation.extension

import android.content.Context
import com.lighthouse.presentation.R
import java.text.DecimalFormat

fun Int.toConcurrency(context: Context, useUnit: Boolean = true): String {
    val format = context.resources.getString(R.string.all_concurrency_format)
    val formattedNumber = DecimalFormat(format).format(this)
    return if (useUnit) {
        context.resources.getString(R.string.all_cash_unit, formattedNumber)
    } else {
        formattedNumber
    }
}
