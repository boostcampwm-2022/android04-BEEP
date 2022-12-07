package com.lighthouse.presentation.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.usecase.setting.GetSecurityOptionUseCase
import com.lighthouse.domain.usecase.setting.SaveSecurityOptionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    getSecurityOptionUseCase: GetSecurityOptionUseCase,
    private val saveSecurityOptionUseCase: SaveSecurityOptionUseCase
) : ViewModel() {

    val securityOption: StateFlow<SecurityOption> =
        getSecurityOptionUseCase().map {
            SecurityOption.values()[it]
        }.stateIn(viewModelScope, SharingStarted.Eagerly, SecurityOption.NONE)

    fun saveSecurityOption(selectedOption: Int) {
        viewModelScope.launch {
            saveSecurityOptionUseCase(selectedOption)
        }
    }
}
