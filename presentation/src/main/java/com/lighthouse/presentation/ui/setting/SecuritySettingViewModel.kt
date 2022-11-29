package com.lighthouse.presentation.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.presentation.util.UserPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecuritySettingViewModel @Inject constructor(
    userPreference: UserPreference
) : ViewModel() {

    private val _securityOptionEventFlow = MutableSharedFlow<Unit>()
    val securityOptionEventFlow = _securityOptionEventFlow.asSharedFlow()

    val securityOptionFlow = userPreference.securityOption

    fun showSecurityOptionDialog() {
        viewModelScope.launch {
            _securityOptionEventFlow.emit(Unit)
        }
    }
}
