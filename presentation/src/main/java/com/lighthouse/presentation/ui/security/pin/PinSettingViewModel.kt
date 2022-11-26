package com.lighthouse.presentation.ui.security.pin

import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.model.UserPreferenceOption
import com.lighthouse.domain.repository.UserPreferencesRepository
import com.lighthouse.domain.usecase.SavePinUseCase
import com.lighthouse.presentation.ui.setting.SecurityOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PinSettingViewModel @Inject constructor(
    private val savePinUseCase: SavePinUseCase,
    private val userPreferencesRepository: UserPreferencesRepository
) : PinViewModel() {

    private var temporaryPinString: String? = null

    init {
        viewModelScope.launch {
            userPreferencesRepository.setIntOption(UserPreferenceOption.SECURITY, SecurityOption.NONE.ordinal)
        }
    }

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
                userPreferencesRepository.setIntOption(UserPreferenceOption.SECURITY, SecurityOption.PIN.ordinal)
            }.onFailure {
                _pinMode.value = PinSettingType.ERROR
            }
        }
    }
}
