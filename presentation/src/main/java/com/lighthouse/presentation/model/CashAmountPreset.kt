package com.lighthouse.presentation.model

import android.content.Context
import com.lighthouse.core.exts.toConcurrency
import com.lighthouse.presentation.R

enum class CashAmountPreset(val amount: Int?) {
    ONE(1000), TWO(5000), THREE(10000), TOTAL(null);

    fun toString(context: Context): String {
        return amount?.toConcurrency()
            ?: context.resources.getString(R.string.use_gifticon_dialog_chip_total_amount)
    }
}
