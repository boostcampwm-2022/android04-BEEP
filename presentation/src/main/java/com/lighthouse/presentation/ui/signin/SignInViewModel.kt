package com.lighthouse.presentation.ui.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.model.UserPreferenceOption
import com.lighthouse.domain.usecase.setting.GetGuestOptionUseCase
import com.lighthouse.domain.usecase.setting.GetOptionStoredUseCase
import com.lighthouse.domain.usecase.setting.SaveGuestOptionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    getOptionStoredUseCase: GetOptionStoredUseCase,
    getGuestOptionUseCase: GetGuestOptionUseCase,
    private val saveGuestOptionUseCase: SaveGuestOptionUseCase
) : ViewModel() {

    val isGuestStored = getOptionStoredUseCase(UserPreferenceOption.GUEST)
    val isGuest = getGuestOptionUseCase()

    fun saveGuestOption(option: Boolean) {
        viewModelScope.launch {
            saveGuestOptionUseCase(option)
        }
    }
}
