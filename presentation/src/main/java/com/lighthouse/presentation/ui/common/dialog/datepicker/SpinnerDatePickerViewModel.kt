package com.lighthouse.presentation.ui.common.dialog.datepicker

import androidx.lifecycle.ViewModel
import com.lighthouse.presentation.extension.toDayOfMonth
import com.lighthouse.presentation.extension.toMonth
import com.lighthouse.presentation.extension.toYear
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar
import java.util.Date
import java.util.Locale

class SpinnerDatePickerViewModel : ViewModel() {

    private val today = Calendar.getInstance(Locale.getDefault())
    var year = today.get(Calendar.YEAR)
    var month = today.get(Calendar.MONTH) + 1
    var dayOfMonth = today.get(Calendar.DAY_OF_MONTH)

    fun setDate(date: Date) {
        year = date.toYear()
        month = date.toMonth()
        dayOfMonth = date.toDayOfMonth()
        _maxDayOfMonth.value = getMaxDayOfMonth(year, month)
    }

    private val _minYear = MutableStateFlow(MIN_YEAR)
    val minYear = _minYear.asStateFlow()

    private val _maxYear = MutableStateFlow(MAX_YEAR)
    val maxYear = _maxYear.asStateFlow()

    private val _minMonth = MutableStateFlow(MIN_MONTH)
    val minMonth = _minMonth.asStateFlow()

    private val _maxMonth = MutableStateFlow(MAX_MONTH)
    val maxMonth = _maxMonth.asStateFlow()

    private val _minDayOfMonth = MutableStateFlow(MIN_DATE)
    val minDayOfMonth = _minDayOfMonth.asStateFlow()

    private val _maxDayOfMonth = MutableStateFlow(MAX_DATE)
    val maxDayOfMonth = _maxDayOfMonth.asStateFlow()

    // 윤년 확인
    private fun getMaxDayOfMonth(year: Int, month: Int): Int {
        return if (month == 2 && year % 4 == 0 && year % 100 != 0) 29 else dayOfMonthPreset[month - 1]
    }

    fun changeYearValue(newValue: Int) {
        year = newValue
        _maxDayOfMonth.value = getMaxDayOfMonth(year, month)
    }

    fun changeMonthValue(newValue: Int) {
        month = newValue
        _maxDayOfMonth.value = getMaxDayOfMonth(year, month)
    }

    fun changeDayOfMonthValue(newValue: Int) {
        dayOfMonth = newValue
    }

    companion object {
        private val dayOfMonthPreset = arrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

        private const val MIN_YEAR = 1900
        private const val MAX_YEAR = 3000

        private const val MIN_MONTH = 1
        private const val MAX_MONTH = 12

        private const val MIN_DATE = 1
        private const val MAX_DATE = 31
    }
}
