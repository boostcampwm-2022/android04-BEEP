package com.lighthouse.presentation.model

import android.content.Context
import com.lighthouse.presentation.R
import com.lighthouse.presentation.extension.toConcurrency

enum class CashAmountPreset(val amount: Int?) {
    ONE(1000), TWO(5000), THREE(10000), TOTAL(null);

    fun toString(context: Context): String {
        return amount?.toConcurrency(context)
            ?: context.resources.getString(R.string.use_gifticon_dialog_chip_total_amount)
    }
}
