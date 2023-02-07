package com.lighthouse.presentation.binding

import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout
import com.lighthouse.presentation.R

@BindingAdapter("concurrencySuffixText")
fun applySuffixTextOrNull(view: TextInputLayout, text: String) {
    view.suffixText =
        if (text.isNotBlank()) view.context.getString(R.string.all_cash_origin_unit) else null
}
