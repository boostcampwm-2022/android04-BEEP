package com.lighthouse.presentation.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.model.UserPreferenceOption
import com.lighthouse.domain.repository.UserPreferencesRepository
import com.lighthouse.presentation.util.UserPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingSecurityViewModel @Inject constructor(
    private val userPreference: UserPreference,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val securityOptionFlow = userPreference.securityOption

    fun saveSecurityOption(selectedOption: Int) {
        viewModelScope.launch {
            userPreferencesRepository.setIntOption(UserPreferenceOption.SECURITY, selectedOption)
        }
    }
}
