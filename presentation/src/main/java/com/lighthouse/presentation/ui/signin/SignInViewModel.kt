package com.lighthouse.presentation.ui.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.model.UserPreferenceOption
import com.lighthouse.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val isGuestStored = userPreferencesRepository.isStored(UserPreferenceOption.GUEST)
    val isGuest = userPreferencesRepository.getBooleanOption(UserPreferenceOption.GUEST)

    fun saveGuestOption(option: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setBooleanOption(UserPreferenceOption.GUEST, option)
        }
    }
}
