package com.lighthouse.presentation.ui.security.pin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.usecase.GetCorrespondWithPinUseCase
import com.lighthouse.domain.usecase.SavePinUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PinViewModel @Inject constructor(
    private val savePinUseCase: SavePinUseCase,
    private val getCorrespondWithPinUseCase: GetCorrespondWithPinUseCase
) : ViewModel() {

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
        when (pinMode.value) {
            PinSettingType.INITIAL -> {
                temporaryPinString = pinString.value
                _pinString.value = ""
                _pinMode.value = PinSettingType.CONFIRM
            }
            else -> {
                if (pinString.value == temporaryPinString) {
                    savePin()
                } else {
                    _pinString.value = ""
                    _pinMode.value = PinSettingType.WRONG
                }
            }
        }
    }

    private fun savePin() {
        viewModelScope.launch {
            savePinUseCase(pinString.value).onSuccess {
                Timber.tag("DATASTORE").d("저장 성공")
                val correct = getCorrespondWithPinUseCase(pinString.value) // TODO: test
                Timber.tag("DATASTORE").d("일치 여부 $correct")
                _pinMode.value = PinSettingType.COMPLETE
            }.onFailure {
                Timber.tag("DATASTORE").d("저장 실패 $it")
            }
        }
    }
}
