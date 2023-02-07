package com.lighthouse.presentation.binding

import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.databinding.BindingAdapter
import com.lighthouse.core.android.utils.resource.AnimInfo
import com.lighthouse.presentation.utils.throttle.OnThrottleClickListener

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

@BindingAdapter(value = ["animation", "animationCondition"], requireAll = false)
fun View.playAnimation(animation: Animation, condition: Boolean? = null) {
    if (condition != false) {
        startAnimation(animation)
    } else {
        clearAnimation()
    }
}

@BindingAdapter("animInfo")
fun View.playAnimationByAnimInfo(animInfo: AnimInfo?) {
    animInfo ?: return

    when (animInfo) {
        is AnimInfo.Empty -> return
        is AnimInfo.AnimResource -> {
            if (animInfo.condition) {
                startAnimation(AnimationUtils.loadAnimation(context, animInfo.resId))
            } else {
                clearAnimation()
            }
        }

        is AnimInfo.DynamicAnim -> {
            if (animInfo.condition) {
                startAnimation(animInfo.animation)
            } else {
                clearAnimation()
            }
        }
    }
}

@BindingAdapter(
    value = ["isAnimatedVisible", "visibleAnimation", "goneAnimation"],
    requireAll = false
)
fun View.playVisibilityAnimation(
    visible: Boolean,
    visibleAnimation: Animation?,
    goneAnimation: Animation?
) {
    if (visible) {
        visibility = View.VISIBLE
        visibleAnimation?.let { startAnimation(it) }
    } else {
        goneAnimation?.let { startAnimation(it) }
        visibility = View.GONE
    }
}
