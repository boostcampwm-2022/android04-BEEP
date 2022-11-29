package com.lighthouse.presentation.ui.setting

import androidx.lifecycle.ViewModel
import com.lighthouse.presentation.util.UserPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingSecurityViewModel @Inject constructor(
    userPreference: UserPreference
) : ViewModel() {

    val securityOptionFlow = userPreference.securityOption
}
