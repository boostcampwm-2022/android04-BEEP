package com.lighthouse.presentation.ui.security.pin

import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.usecase.GetCorrespondWithPinUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
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
        Timber.tag("WHY").d("PinDialogViewModel: goNextStep 호출 ${pinString.value} ${pinMode.value}")
        Timber.tag("WHY").d("PinDialogViewModel: viewModelScope.launch{} ${viewModelScope.launch{}}")
        viewModelScope.launch {
            Timber.tag("WHY").d("PinDialogViewModel: 여기")
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
