package com.lighthouse.presentation.ui.security.pin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.model.UserPreferenceOption
import com.lighthouse.domain.repository.UserPreferencesRepository
import com.lighthouse.domain.usecase.SavePinUseCase
import com.lighthouse.presentation.ui.setting.SecurityOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PinViewModel @Inject constructor(
    private val savePinUseCase: SavePinUseCase,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _pinString = MutableStateFlow("")
    val pinString = _pinString.asStateFlow()

    private val _pinMode = MutableStateFlow(PinSettingType.INITIAL)
    val pinMode = _pinMode.asStateFlow()

    private var temporaryPinString: String? = null

    init {
        viewModelScope.launch {
            userPreferencesRepository.setIntOption(UserPreferenceOption.SECURITY, SecurityOption.NONE.ordinal)
        }
    }

    fun inputPin(num: Int) {
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

    fun goPreviousStep() {
        _pinString.value = ""
        _pinMode.value = PinSettingType.INITIAL
    }

    private fun goNextStep() {
        viewModelScope.launch {
            when (pinMode.value) {
                PinSettingType.INITIAL -> {
                    temporaryPinString = pinString.value
                    delay(500L)
                    _pinString.value = ""
                    _pinMode.value = PinSettingType.CONFIRM
                }
                else -> {
                    if (pinString.value == temporaryPinString) {
                        savePin()
                    } else {
                        _pinMode.value = PinSettingType.WRONG
                        delay(1000L)
                        _pinString.value = ""
                        _pinMode.value = PinSettingType.CONFIRM
                    }
                }
            }
        }
    }

    private fun savePin() {
        viewModelScope.launch {
            savePinUseCase(pinString.value).onSuccess {
                _pinMode.value = PinSettingType.COMPLETE
                delay(1000L)
                userPreferencesRepository.setIntOption(UserPreferenceOption.SECURITY, SecurityOption.PIN.ordinal)
            }.onFailure {
                _pinMode.value = PinSettingType.ERROR
            }
        }
    }
}
