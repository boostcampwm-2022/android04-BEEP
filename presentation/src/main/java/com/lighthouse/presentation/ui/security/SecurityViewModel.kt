package com.lighthouse.presentation.ui.security

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.usecase.setting.SaveSecurityOptionUseCase
import com.lighthouse.presentation.extra.Extras
import com.lighthouse.presentation.ui.security.event.SecurityDirections
import com.lighthouse.beep.model.user.SecurityOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val saveSecurityOptionUseCase: SaveSecurityOptionUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val directionsFlow = MutableSharedFlow<SecurityDirections>()
    val isRevise = savedStateHandle.get<Boolean>(Extras.KEY_PIN_REVISE) ?: false

    fun gotoOtherScreen(directions: SecurityDirections) {
        viewModelScope.launch {
            directionsFlow.emit(directions)
        }
    }

    fun setSecurityOption(securityOption: SecurityOption) {
        viewModelScope.launch {
            saveSecurityOptionUseCase(securityOption.ordinal)
        }
    }
}
