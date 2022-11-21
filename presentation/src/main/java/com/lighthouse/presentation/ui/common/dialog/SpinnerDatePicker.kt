package com.lighthouse.presentation.ui.common.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.DialogSpinnerDatePickerBinding
import com.lighthouse.presentation.extension.toDate
import com.lighthouse.presentation.extension.toMonth
import com.lighthouse.presentation.extension.toYear
import java.util.Date

class SpinnerDatePicker(
    private val context: Context,
    private val themeResId: Int? = null,
    private val onClickOk: (picker: SpinnerDatePicker, year: Int, month: Int, dayOfMonth: Int) -> Unit
) {

    constructor(
        context: Context,
        initYear: Int,
        initMonth: Int,
        initDayOfMonth: Int,
        themeResId: Int? = null,
        onClickOk: (picker: SpinnerDatePicker, year: Int, month: Int, dayOfMonth: Int) -> Unit
    ) : this(context, themeResId, onClickOk) {
        setDate(initYear, initMonth, initDayOfMonth)
    }

    constructor(
        context: Context,
        initDate: Date,
        themeResId: Int? = null,
        onClickOk: (picker: SpinnerDatePicker, year: Int, month: Int, dayOfMonth: Int) -> Unit
    ) : this(
        context,
        themeResId,
        onClickOk
    ) {
        setDate(initDate)
    }

    private val binding: DialogSpinnerDatePickerBinding =
        DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_spinner_date_picker, null, false)

    private val dialog = run {
        val theme = themeResId ?: R.style.Theme_BEEP_DatePicker
        AlertDialog.Builder(context, theme).setView(binding.root).create()
    }

    private val tvYear = binding.npYear.tvText
    private val tvMonth = binding.npMonth.tvText
    private val tvDate = binding.npDate.tvText
    private val npYear = binding.npYear.npPicker
    private val npMonth = binding.npMonth.npPicker
    private val npDate = binding.npDate.npPicker

    init {
        setLabel()
        initPicker()
        binding.btnOk.setOnClickListener {
            onClickOk(this, npYear.value, npMonth.value, npDate.value)
        }
    }

    /**
     * 연, 월, 일 라벨 텍스트를 설정한다
     * */
    fun setLabel(year: String? = null, month: String? = null, dayOfMonth: String? = null) {
        tvYear.text = year ?: context.getString(R.string.all_year)
        tvMonth.text = month ?: context.getString(R.string.all_month)
        tvDate.text = dayOfMonth ?: context.getString(R.string.all_day)
    }

    /**
     * 처음에 보여 줄 날짜를 설정한다
     * */
    fun setDate(date: Date) {
        setDate(date.toYear(), date.toMonth(), date.toDate())
    }

    /**
     * 처음에 보여 줄 날짜를 설정한다. 표현할 수 있는 날짜를 벗어난 경우 현재 정보를 표시한다
     * */
    fun setDate(year: Int? = null, month: Int? = null, dayOfMonth: Int? = null) {
        val today = Date()
        npYear.value = checkAndGetIntRange(year ?: today.toYear(), MIN_YEAR, MAX_YEAR, today.toYear())
        npMonth.value = checkAndGetIntRange(month ?: today.toMonth(), MIN_MONTH, MAX_MONTH, today.toMonth())
        npDate.value = checkAndGetIntRange(dayOfMonth ?: today.toDate(), MIN_DATE, npDate.maxValue, today.toDate())
        npDate.maxValue = getMaxDayOfMonth(npYear.value, npMonth.value)
    }

    /**
     * 다이얼로그의 제목을 설정한다
     * */
    fun setTitle(@StringRes title: Int) {
        dialog.setTitle(title)
    }

    /**
     * 다이얼로그의 제목을 설정한다
     * */
    fun setTitle(title: String) {
        dialog.setTitle(title)
    }

    /**
     * 생성된 다이얼로그 객체를 반환한다
     * */
    fun create(): AlertDialog {
        return dialog
    }

    /**
     * 다이얼로그를 띄운다
     * */
    fun show() {
        dialog.show()
    }

    /**
     * 다이얼로그를 닫는다
     * */
    fun dismiss() {
        dialog.dismiss()
    }

    private fun initPicker() {
        // 초기 값 설정
        npYear.minValue = MIN_YEAR
        npMonth.minValue = MIN_MONTH
        npDate.minValue = MIN_DATE

        npYear.maxValue = MAX_YEAR
        npMonth.maxValue = MAX_MONTH
        npDate.maxValue = MAX_DATE

        // 끝에서 처음으로 순환되지 않도록 하는 설정
        npYear.wrapSelectorWheel = false
        npMonth.wrapSelectorWheel = false
        npDate.wrapSelectorWheel = false

        // 연, 월에 따라 일 수 조정하기 위한 설정
        npYear.setOnValueChangedListener { _, _, _ ->
            npDate.maxValue = getMaxDayOfMonth(npYear.value, npMonth.value)
        }
        npMonth.setOnValueChangedListener { _, _, _ ->
            npDate.maxValue = getMaxDayOfMonth(npYear.value, npMonth.value)
        }
        npDate.setOnValueChangedListener { _, _, _ ->
            npDate.maxValue = getMaxDayOfMonth(npYear.value, npMonth.value)
        }
    }

    private fun getMaxDayOfMonth(year: Int, month: Int): Int {
        return if (month == 2 && year % 4 == 0 && year % 100 != 0) 29 else preset[month - 1] // 윤년 확인
    }

    private fun checkAndGetIntRange(target: Int, start: Int, endInclude: Int, defaultValue: Int): Int {
        return if (target in start..endInclude) target else defaultValue
    }

    companion object {
        val preset = arrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

        const val MIN_YEAR = 1900
        const val MAX_YEAR = 3000
        const val MIN_MONTH = 1
        const val MAX_MONTH = 12
        const val MIN_DATE = 1
        const val MAX_DATE = 31
    }
}
