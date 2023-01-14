package com.lighthouse.presentation.util.resource

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.lighthouse.presentation.util.resource.UIText.Empty.asString

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

    class Builder {
        private val texts = arrayListOf<UIText>()
        private val uiSpans = arrayListOf<UISpan>()
        private fun applySpan(uiSpan: UISpan) = apply { uiSpans.add(uiSpan) }

        fun appendText(uiText: UIText) = apply { texts.add(uiText) }
        fun appendDynamicString(any: Any) = apply { appendText(DynamicString(any)) }
        fun appendStringResource(
            @StringRes resId: Int,
            vararg args: Any
        ) = apply { appendText(StringResource(resId, args)) }

        fun applyUnderlineSpan() = apply { applySpan(UISpan.UIUnderlineSpan) }
        fun applyBoldSpan() = apply { applySpan(UISpan.UIBoldSpan) }
        fun applyTextColorSpan(@ColorRes colorRes: Int) =
            apply { applySpan(UISpan.UITextColorSpan(colorRes)) }

        fun applyBackgroundColorSpan(@ColorRes colorRes: Int) =
            apply { applySpan(UISpan.UIBackgroundColorSpan(colorRes)) }

        fun applyRelativeSizeSpan(factor: Float) =
            apply { applySpan(UISpan.UIRelativeSizeSpan(factor)) }

        fun applyClickableSpan(onClick: () -> Unit) =
            apply { applySpan(UISpan.UIClickableSpan(onClick)) }

        fun build(): UIText {
            val uiTextSet = UITextSet(*texts.toTypedArray())
            return if (uiSpans.isEmpty()) {
                uiTextSet
            } else {
                SpannableResource(uiTextSet, *uiSpans.toTypedArray())
            }
        }
    }
}
