package com.lighthouse.presentation.ui.security.pin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class PinViewModel : ViewModel() {

    protected val _pinString = MutableStateFlow("")
    val pinString = _pinString.asStateFlow()

    protected val _pinMode = MutableStateFlow(PinSettingType.INITIAL)
    val pinMode = _pinMode.asStateFlow()

    protected val _pushedNum = MutableSharedFlow<Int>()
    val pushedNum = _pushedNum.asSharedFlow()

    fun inputPin(num: Int) {
        viewModelScope.launch { _pushedNum.emit(num) }

        if (pinMode.value == PinSettingType.WAIT || pinMode.value == PinSettingType.WRONG) return

        if (pinString.value.length < 6) {
            _pinString.value = "${pinString.value}$num"
        }

        if (pinString.value.length == 6) {
            goNextStep()
        }
    }

    fun removePin() {
        viewModelScope.launch { _pushedNum.emit(10) }

        if (pinString.value.isNotEmpty()) {
            _pinString.value = pinString.value.dropLast(1)
        }
    }

    abstract fun goPreviousStep()

    abstract fun goNextStep()
}
