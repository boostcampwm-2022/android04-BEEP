package com.lighthouse.presentation.extension

import android.view.View
import android.widget.ScrollView

fun ScrollView.scrollToBottom() {
    this.post {
        this.fullScroll(View.FOCUS_DOWN)
    }
}
