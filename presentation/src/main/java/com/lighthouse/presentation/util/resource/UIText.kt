package com.lighthouse.presentation.util.resource

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import java.lang.ref.WeakReference
import java.text.DecimalFormat

sealed class UIText(
    val clickable: Boolean = false,
) {

    private lateinit var spannableRef: WeakReference<Spannable>

    fun asString(context: Context): Spannable {
        val cache = if (this::spannableRef.isInitialized) spannableRef.get() else null

        return cache ?: makeSpannable(context).also {
            spannableRef = WeakReference(it)
        }
    }

    abstract fun makeSpannable(context: Context): Spannable

    object Empty : UIText() {
        override fun makeSpannable(context: Context): Spannable {
            return SpannableString("")
        }
    }

    data class DynamicString(private val any: Any) : UIText() {
        override fun makeSpannable(context: Context): Spannable = SpannableString(any.toString())
    }

    class StringResource(
        @StringRes private val resId: Int,
        private vararg val args: Any,
    ) : UIText() {
        override fun makeSpannable(context: Context): Spannable =
            SpannableString(context.getString(resId, *args))

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is StringResource) return false

            return resId == other.resId && args.contentEquals(other.args)
        }

        override fun hashCode(): Int {
            return 31 * resId.hashCode() + args.contentHashCode()
        }
    }

    class CurrencyString(
        private val number: Float,
        @StringRes private val resId: Int? = null,
        private val format: String? = null,
    ) : UIText() {

        constructor(
            number: Int,
            @StringRes resId: Int? = null,
            format: String? = null,
        ) : this(number.toFloat(), resId, format)

        override fun makeSpannable(context: Context): Spannable {
            val numberFormat = format
                ?: if (number == number.toInt().toFloat()) {
                    "#,###"
                } else {
                    "#,###.##"
                }
            val formatter = DecimalFormat(numberFormat)
            val formatted = formatter.format(number)
            return resId?.let { SpannableString(context.getString(it, formatted)) } ?: SpannableString(formatted)
        }
    }

    class SpannableResource(
        private val text: UIText,
        private vararg val spans: UISpan,
    ) : UIText(
        spans.any { it is UISpan.UIClickableSpan },
    ) {
        override fun makeSpannable(context: Context): Spannable {
            val spannable = text.makeSpannable(context)
            spans.forEach {
                spannable.setSpan(
                    it.asSpan(context),
                    0,
                    spannable.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
                )
            }
            return spannable
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is SpannableResource) return false

            return text == other.text && spans.contentEquals(other.spans)
        }

        override fun hashCode(): Int {
            return 31 * text.hashCode() + spans.contentHashCode()
        }
    }

    class UITextSet(
        private vararg val texts: UIText,
    ) : UIText(
        texts.any { it.clickable },
    ) {
        override fun makeSpannable(context: Context): Spannable {
            val builder = SpannableStringBuilder()
            texts.forEach {
                builder.append(it.makeSpannable(context))
            }
            return builder
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is UITextSet) return false

            return texts.contentEquals(other.texts)
        }

        override fun hashCode(): Int {
            return texts.contentHashCode()
        }
    }

    class Builder {
        private val texts = arrayListOf<UIText>()
        private val spans = arrayListOf<UISpan>()

        private fun spanOn(uiSpan: UISpan) = apply { spans.add(uiSpan) }
        private fun appendText(uiText: UIText) = apply {
            applySpan()
            texts.add(uiText)
        }

        private fun applySpan() {
            val lastIndex = texts.lastIndex
            if (lastIndex != -1 && spans.isNotEmpty()) {
                val lastText = texts[lastIndex]
                texts[lastIndex] =
                    SpannableResource(lastText, *spans.toTypedArray())
                spans.clear()
            }
        }

        fun appendDynamicString(any: Any) =
            apply { appendText(DynamicString(any)) }

        fun appendStringResource(
            @StringRes resId: Int,
            vararg args: Any,
        ) = apply { appendText(StringResource(resId, args)) }

        fun spanOnUnderline() =
            apply { spanOn(UISpan.UIUnderlineSpan) }

        fun spanOnBold() =
            apply { spanOn(UISpan.UIBoldSpan) }

        fun spanOnTextColor(@ColorRes colorRes: Int) =
            apply { spanOn(UISpan.UITextColorSpan(colorRes)) }

        fun spanOnBackgroundColor(@ColorRes colorRes: Int) =
            apply { spanOn(UISpan.UIBackgroundColorSpan(colorRes)) }

        fun spanOnRelativeSize(factor: Float) =
            apply { spanOn(UISpan.UIRelativeSizeSpan(factor)) }

        fun spanOnClickable(onClick: () -> Unit) =
            apply { spanOn(UISpan.UIClickableSpan(onClick)) }

        fun build(): UIText {
            applySpan()
            return UITextSet(*texts.toTypedArray())
        }
    }
}
