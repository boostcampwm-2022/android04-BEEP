package com.lighthouse.presentation.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.model.UserPreferenceOption
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class SettingViewModel : ViewModel() {

    private val _optionFlow = MutableSharedFlow<UserPreferenceOption>()
    val optionFlow = _optionFlow.asSharedFlow()

    fun setSettingOption(option: UserPreferenceOption) {
        Timber.tag("SETTING").d(option.name)
        viewModelScope.launch {
            _optionFlow.emit(option)
        }
    }
}
