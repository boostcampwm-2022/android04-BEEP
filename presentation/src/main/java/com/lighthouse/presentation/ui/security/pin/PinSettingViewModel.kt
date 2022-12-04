package com.lighthouse.presentation.ui.security.pin

import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.usecase.setting.SavePinUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PinSettingViewModel @Inject constructor(
    private val savePinUseCase: SavePinUseCase
) : PinViewModel() {

    private var temporaryPinString: String? = null

    override fun goPreviousStep() {
        _pinString.value = ""
        _pinMode.value = PinSettingType.INITIAL
    }

    override fun goNextStep() {
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
            }.onFailure {
                _pinMode.value = PinSettingType.ERROR
            }
        }
    }
}
