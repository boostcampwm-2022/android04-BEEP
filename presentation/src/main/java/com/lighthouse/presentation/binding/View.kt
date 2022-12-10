package com.lighthouse.presentation.binding

import android.view.View
import android.view.animation.AnimationUtils
import androidx.databinding.BindingAdapter
import com.lighthouse.presentation.R
import com.lighthouse.presentation.util.OnThrottleClickListener

@BindingAdapter("isVisible")
fun applyVisibility(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("onThrottleClickListener")
fun View.setOnThrottleClickListener(listener: (View) -> Unit) {
    setOnClickListener(object : OnThrottleClickListener() {
        override fun onThrottleClick(view: View) {
            listener(view)
        }
    })
}

@BindingAdapter("isStamp")
fun playStampAnimation(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
    if (visible) {
        val animation = AnimationUtils.loadAnimation(view.context, R.anim.anim_stamp)
        view.startAnimation(animation)
    } else {
        val animation = AnimationUtils.loadAnimation(view.context, R.anim.anim_fade_out)
        view.startAnimation(animation)
    }
}
