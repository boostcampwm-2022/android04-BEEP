package com.lighthouse.presentation.ui.security.pin

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PinViewModel : ViewModel() {

    private val _pinString = MutableStateFlow("")
    val pinString = _pinString.asStateFlow()

    fun inputPin(num: Int) {
        if (pinString.value.length < 6) {
            _pinString.update { pinString.value + num.toString() }
        }
    }

    fun removePin() {
        if (pinString.value.isNotEmpty()) {
            _pinString.update { pinString.value.slice(0 until pinString.value.length - 1) }
        }
    }
}
