package com.lighthouse.presentation.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.usecase.setting.SaveSecurityOptionUseCase
import com.lighthouse.presentation.util.UserPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    userPreference: UserPreference,
    private val saveSecurityOptionUseCase: SaveSecurityOptionUseCase
) : ViewModel() {

    val securityOptionFlow = userPreference.securityOption

    fun saveSecurityOption(selectedOption: Int) {
        viewModelScope.launch {
            saveSecurityOptionUseCase(selectedOption)
        }
    }
}
