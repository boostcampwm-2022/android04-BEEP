package com.lighthouse.presentation.ui.security.pin

import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.usecase.setting.GetCorrespondWithPinUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PinDialogViewModel @Inject constructor(
    private val getCorrespondWithPinUseCase: GetCorrespondWithPinUseCase
) : PinViewModel() {

    init {
        _pinMode.value = PinSettingType.CONFIRM
        _pinString.value = ""
    }

    override fun goPreviousStep() {
    }

    override fun goNextStep() {
        viewModelScope.launch {
            if (getCorrespondWithPinUseCase(pinString.value)) {
                _pinMode.value = PinSettingType.COMPLETE
            } else {
                _pinMode.value = PinSettingType.WRONG
            }
            delay(1000L)
            _pinString.value = ""
            _pinMode.value = PinSettingType.CONFIRM
        }
    }
}
