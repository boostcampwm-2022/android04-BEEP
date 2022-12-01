package com.lighthouse.presentation.ui.security

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.model.UserPreferenceOption
import com.lighthouse.domain.repository.UserPreferencesRepository
import com.lighthouse.presentation.ui.security.event.SecurityDirections
import com.lighthouse.presentation.ui.setting.SecurityOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val directionsFlow = MutableSharedFlow<SecurityDirections>()
    val isRevise = savedStateHandle.get<Boolean>("revise") ?: false

    fun gotoOtherScreen(directions: SecurityDirections) {
        viewModelScope.launch {
            directionsFlow.emit(directions)
        }
    }

    fun setSecurityOption(securityOption: SecurityOption) {
        viewModelScope.launch {
            userPreferencesRepository.setIntOption(UserPreferenceOption.SECURITY, securityOption.ordinal)
        }
    }
}
