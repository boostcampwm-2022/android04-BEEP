package com.lighthouse.presentation.ui.common.view

import android.content.Context
import android.text.Spannable
import android.text.method.MovementMethod
import android.util.AttributeSet
import android.widget.HorizontalScrollView
import android.widget.TextView
import com.lighthouse.presentation.R

class ScrollableTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : HorizontalScrollView(context, attrs, android.R.attr.horizontalScrollViewStyle) {

    private var textView: TextView

    init {
        isVerticalScrollBarEnabled = false
        isHorizontalScrollBarEnabled = false
        overScrollMode = OVER_SCROLL_NEVER

        textView = TextView(context, attrs, defStyleAttr).apply {
            maxLines = 1
        }

        context.obtainStyledAttributes(attrs, R.styleable.ScrollableTextView).apply {
            try {
                val text = getText(R.styleable.ScrollableTextView_android_text)
                if (isInEditMode && text != null) {
                    textView.text = text
                }
            } finally {
                recycle()
                addView(textView)
            }
        }
    }

    fun setText(text: String?) {
        textView.text = text ?: ""
    }

    fun setText(text: Spannable?) {
        textView.text = text ?: ""
    }

    fun setMovementMethod(movementMethod: MovementMethod) {
        textView.movementMethod = movementMethod
    }
}
