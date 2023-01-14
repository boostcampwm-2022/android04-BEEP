package com.lighthouse.presentation.binding

import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.lighthouse.presentation.R
import com.lighthouse.presentation.extension.toDayOfMonth
import com.lighthouse.presentation.extension.toMonth
import com.lighthouse.presentation.extension.toYear
import com.lighthouse.presentation.util.TimeCalculator
import com.lighthouse.presentation.util.resource.UIText
import java.text.DecimalFormat
import java.util.Date

@BindingAdapter("dateFormat")
fun applyDateFormat(view: TextView, date: Date?) {
    date ?: return
    view.text = view.context.getString(
        R.string.all_date,
        date.toYear(),
        date.toMonth(),
        date.toDayOfMonth()
    )
}

@BindingAdapter("concurrencyFormat")
fun applyConcurrencyFormat(view: TextView, amount: Int) {
    val format = view.context.resources.getString(R.string.all_concurrency_format)
    val formattedNumber = DecimalFormat(format).format(amount)
    view.text = if (amount > 0) {
        view.context.resources.getString(
            R.string.all_cash_unit,
            formattedNumber
        )
    } else {
        ""
    }
}

@BindingAdapter("setUIText")
fun TextView.setUIText(uiText: UIText?) {
    if (uiText == null) return
    if (uiText.clickable) {
        movementMethod = LinkMovementMethod()
    }
    text = uiText.asString(context)
}

/**
 * 타겟 텍스트에 밑줄을 칠한다. 타겟 텍스트를 찾지 못 한 경우 무시된다
 *
 * @param targetText 밑줄을 칠할 텍스트
 */
@BindingAdapter("underLineText")
fun applyUnderLine(view: TextView, targetText: String) {
    val start = view.text.indexOf(string = targetText, ignoreCase = false)
    if (start == -1) return
    val end = start + targetText.length

    view.text = SpannableStringBuilder(view.text).apply {
        setSpan(UnderlineSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}

/**
 * 부분적으로 클릭할 수 있다. 타겟 텍스트를 찾지 못 한 경우 무시된다
 *
 * @param targetText 클릭 가능한 문구
 * @param onClickListener 클릭 리스너
 * @param drawUnderLine 텍스트 강조 여부
 */
@BindingAdapter("clickableText", "clickableClicked", "drawUnderLine", requireAll = false)
fun TextView.applyClickable(
    targetText: String,
    onClickListener: View.OnClickListener,
    drawUnderLine: Boolean? = null
) {
    val start = text.indexOf(string = targetText, ignoreCase = false)

    if (start == -1) return
    val end = start + targetText.length

    val spannableString = SpannableString(text).apply {
        setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    onClickListener.onClick(widget)
                }

                override fun updateDrawState(ds: TextPaint) {
                    if (drawUnderLine != null && drawUnderLine == false) return
                    super.updateDrawState(ds)
                }
            },
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    movementMethod = LinkMovementMethod()
    setText(spannableString, TextView.BufferType.SPANNABLE)
}

@BindingAdapter("setDday")
fun setDday(view: TextView, date: Date) {
    val dDay = TimeCalculator.formatDdayToInt(date.time)
    view.text = when {
        dDay == TimeCalculator.MIN_DAY -> view.context.getString(R.string.all_d_very_day)
        dDay in TimeCalculator.MIN_DAY until TimeCalculator.MAX_DAY -> String.format(
            view.context.getString(R.string.all_d_day),
            dDay
        )
        dDay < TimeCalculator.MIN_DAY -> view.context.getString(R.string.all_d_day_expired)
        else -> view.context.getString(R.string.all_d_day_more_than_year)
    }
}
