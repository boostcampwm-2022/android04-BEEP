package com.lighthouse.presentation.util.resource

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import androidx.annotation.StringRes

sealed class UIText {

    abstract fun asString(context: Context): Spannable

    object Empty : UIText() {
        override fun asString(context: Context): Spannable = SpannableString("")
    }

    data class DynamicString(private val any: Any) : UIText() {
        override fun asString(context: Context): Spannable = SpannableString(any.toString())
    }

    class StringResource(
        @StringRes private val resId: Int,
        private vararg val args: Any
    ) : UIText() {
        override fun asString(context: Context): Spannable =
            SpannableString(context.getString(resId, *args))
    }

    class SpannableResource(
        private val text: UIText,
        private vararg val spans: UISpan
    ) : UIText() {
        override fun asString(context: Context): Spannable {
            val spannable = text.asString(context)
            spans.forEach {
                spannable.setSpan(
                    it.asSpan(context),
                    0,
                    spannable.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            return spannable
        }
    }

    class UITextSet(
        private vararg val texts: UIText
    ) : UIText() {
        override fun asString(context: Context): Spannable {
            val builder = SpannableStringBuilder()
            texts.forEach {
                builder.append(it.asString(context))
            }
            return builder
        }
    }
}
