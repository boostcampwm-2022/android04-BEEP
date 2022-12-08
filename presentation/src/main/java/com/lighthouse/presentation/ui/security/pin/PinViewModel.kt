package com.lighthouse.presentation.ui.security.pin

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class PinViewModel : ViewModel() {

    protected val _pinString = MutableStateFlow("")
    val pinString = _pinString.asStateFlow()

    protected val _pinMode = MutableStateFlow(PinSettingType.INITIAL)
    val pinMode = _pinMode.asStateFlow()

    fun inputPin(num: Int) {
        if (pinMode.value == PinSettingType.WAIT || pinMode.value == PinSettingType.WRONG) return

        if (pinString.value.length < 6) {
            _pinString.value = "${pinString.value}$num"
        }

        if (pinString.value.length == 6) {
            goNextStep()
        }
    }

    fun removePin() {
        if (pinString.value.isNotEmpty()) {
            _pinString.value = pinString.value.dropLast(1)
        }
    }

    abstract fun goPreviousStep()

    abstract fun goNextStep()
}
