package com.lighthouse.presentation.utils.throttle

import android.view.View
import android.view.View.OnClickListener

abstract class OnThrottleClickListener : OnClickListener {

    private var recentClickTime: Long = 0L

    private val isSafe: Boolean
        get() = System.currentTimeMillis() - recentClickTime > THROTTLE_TIME

    override fun onClick(view: View) {
        if (isSafe) {
            recentClickTime = System.currentTimeMillis()
            onThrottleClick(view)
        }
    }

    abstract fun onThrottleClick(view: View)

    companion object {
        private const val THROTTLE_TIME = 500L
    }
}
