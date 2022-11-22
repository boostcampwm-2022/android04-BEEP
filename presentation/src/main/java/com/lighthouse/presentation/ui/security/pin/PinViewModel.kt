package com.lighthouse.presentation.ui.security.pin

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PinViewModel : ViewModel() {

    private val _pinString = MutableStateFlow("")
    val pinString = _pinString.asStateFlow()

    private val _pinMode = MutableStateFlow(PinSettingType.INITIAL)
    val pinMode = _pinMode.asStateFlow()

    private var temporaryPinString: String? = null

    fun inputPin(num: Int) {
        if (pinString.value.length < 6) {
            _pinString.value = "${pinString.value}$num"
        }

        if (pinString.value.length == 6) {
            goNext()
        }
    }

    fun removePin() {
        if (pinString.value.isNotEmpty()) {
            _pinString.value = pinString.value.substring(0, pinString.value.length - 1)
        }
    }

    fun goPrevious() {
        _pinString.value = ""
        _pinMode.value = PinSettingType.INITIAL
    }

    private fun goNext() {
        when (pinMode.value) {
            PinSettingType.INITIAL -> {
                temporaryPinString = pinString.value
                _pinString.value = ""
                _pinMode.value = PinSettingType.CONFIRM
            }
            else -> {
                if (pinString.value == temporaryPinString) {
                    // TODO: 저장
                    _pinMode.value = PinSettingType.COMPLETE
                } else {
                    _pinString.value = ""
                    _pinMode.value = PinSettingType.WRONG
                }
            }
        }
    }
}
