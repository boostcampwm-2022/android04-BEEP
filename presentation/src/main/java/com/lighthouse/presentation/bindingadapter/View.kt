package com.lighthouse.presentation.bindingadapter

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("isVisible")
fun applyVisibility(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}
