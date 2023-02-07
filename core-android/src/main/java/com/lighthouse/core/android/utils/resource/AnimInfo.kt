package com.lighthouse.core.android.utils.resource

import android.view.animation.Animation
import androidx.annotation.AnimRes

sealed class AnimInfo {

    object Empty : AnimInfo()

    data class DynamicAnim(val animation: Animation, val condition: Boolean) : AnimInfo()

    data class AnimResource(
        @AnimRes val resId: Int,
        val condition: Boolean
    ) : AnimInfo()
}
