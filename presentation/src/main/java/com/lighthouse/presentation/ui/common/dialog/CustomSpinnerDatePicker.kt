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

class CustomSpinnerDatePicker(
    private val context: Context,
    private val onClickOk: (picker: CustomSpinnerDatePicker, year: Int, month: Int, dayOfMonth: Int) -> Unit
) {
    private val binding: DialogSpinnerDatePickerBinding by lazy {
        DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_spinner_date_picker, null, false)
    }
    private val dialog = AlertDialog.Builder(context).setView(binding.root)
    private lateinit var instance: AlertDialog

    init {
        initPicker()
        binding.btnOk.setOnClickListener {
            onClickOk(this, binding.npYear.value + 1, binding.npMonth.value + 1, binding.npDate.value + 1)
        }
    }

    fun setDate(date: Date) {
        setDate(date.toYear(), date.toMonth(), date.toDate())
    }

    fun setDate(year: Int, month: Int, dayOfMonth: Int) {
        binding.npYear.value = year - 1
        binding.npMonth.value = month - 1
        binding.npDate.value = dayOfMonth - 1
    }

    fun setTitle(@StringRes title: Int) {
        dialog.setTitle(title)
    }

    fun create(): AlertDialog {
        return dialog.create().also {
            if (this::instance.isInitialized.not()) {
                instance = it
            }
        }
    }

    fun show(): AlertDialog {
        return dialog.show().also {
            if (this::instance.isInitialized.not()) {
                instance = it
            }
        }
    }

    fun dismiss() {
        instance.dismiss()
    }

    private fun initPicker() {
        with(binding) {
            // 초기 값 설정
            npYear.displayedValues = Array(3000) { context.getString(R.string.all_year, it + 1) }
            npMonth.displayedValues = Array(12) { context.getString(R.string.all_month, it + 1) }
            npDate.displayedValues = Array(31) { context.getString(R.string.all_day, it + 1) }

            npYear.maxValue = 2999
            npMonth.maxValue = 11
            npDate.maxValue = 30

            // 끝에서 처음으로 순환되지 않도록 하는 설정
            npYear.wrapSelectorWheel = false
            npMonth.wrapSelectorWheel = false
            npDate.wrapSelectorWheel = false

            // TODO 화면에 보여지는 일도 연, 월에 따라 변경 필요
            // 연, 월에 따라 일 수 조정하기 위한 설정
            npYear.setOnValueChangedListener { _, _, _ ->
                npDate.maxValue = getMaxDayOfMonth(npYear.value, npMonth.value)
            }
            npMonth.setOnValueChangedListener { _, _, _ ->
                npDate.maxValue = getMaxDayOfMonth(npYear.value, npMonth.value)
            }
        }
    }

    private fun getMaxDayOfMonth(year: Int, month: Int): Int {
        val preset = arrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

        return if (year % 4 == 0 && year % 100 != 0 && month == 2) 29 else preset[month - 1] // 윤년 확인
    }
}
