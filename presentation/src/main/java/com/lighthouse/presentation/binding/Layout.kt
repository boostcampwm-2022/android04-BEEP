package com.lighthouse.presentation.binding

import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.facebook.shimmer.ShimmerFrameLayout

@BindingAdapter("setShimmerState")
fun setShimmerState(layout: ShimmerFrameLayout, state: Boolean) {
    layout.isVisible = state

    when (state) {
        true -> layout.startShimmer()
        false -> layout.stopShimmer()
    }
}
