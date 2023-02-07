package com.lighthouse.core.android.utils.resource

import android.content.Context
import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

sealed class UISpan {

    abstract fun asSpan(context: Context): Any

    object UIUnderlineSpan : UISpan() {
        override fun asSpan(context: Context): Any {
            return UnderlineSpan()
        }
    }

    object UIBoldSpan : UISpan() {
        override fun asSpan(context: Context): Any {
            return StyleSpan(Typeface.BOLD)
        }
    }

    data class UITextColorSpan(@ColorRes private val colorRes: Int) : UISpan() {
        override fun asSpan(context: Context): Any {
            return ForegroundColorSpan(ContextCompat.getColor(context, colorRes))
        }
    }

    data class UIBackgroundColorSpan(@ColorRes private val colorRes: Int) : UISpan() {
        override fun asSpan(context: Context): Any {
            return BackgroundColorSpan(ContextCompat.getColor(context, colorRes))
        }
    }

    data class UIRelativeSizeSpan(private val factor: Float) : UISpan() {
        override fun asSpan(context: Context): Any {
            return RelativeSizeSpan(factor)
        }
    }

    data class UIClickableSpan(private val onClick: () -> Unit) : UISpan() {
        override fun asSpan(context: Context): Any {
            return object : ClickableSpan() {
                override fun onClick(widget: View) {
                    onClick()
                }

                override fun updateDrawState(ds: TextPaint) = Unit
            }
        }
    }
}
