package com.lighthouse.presentation.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.model.UserPreferenceOption
import com.lighthouse.presentation.util.UserPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    userPreference: UserPreference
) : ViewModel() {

    private val _optionFlow = MutableSharedFlow<UserPreferenceOption>()
    val optionFlow = _optionFlow.asSharedFlow()

    val securityOptionFlow = userPreference.securityOption.map { it.text }.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    fun setSettingOption(option: UserPreferenceOption) {
        viewModelScope.launch {
            _optionFlow.emit(option)
        }
    }
}
