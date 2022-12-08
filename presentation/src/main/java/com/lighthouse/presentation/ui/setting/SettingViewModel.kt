package com.lighthouse.presentation.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.usecase.MoveUserIdGifticonUseCase
import com.lighthouse.domain.usecase.setting.GetGuestOptionUseCase
import com.lighthouse.domain.usecase.setting.GetNotificationOptionUseCase
import com.lighthouse.domain.usecase.setting.GetSecurityOptionUseCase
import com.lighthouse.domain.usecase.setting.MoveGuestDataUseCase
import com.lighthouse.domain.usecase.setting.SaveGuestOptionUseCase
import com.lighthouse.domain.usecase.setting.SaveNotificationOptionUseCase
import com.lighthouse.domain.usecase.setting.SaveSecurityOptionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    getSecurityOptionUseCase: GetSecurityOptionUseCase,
    private val saveSecurityOptionUseCase: SaveSecurityOptionUseCase,
    getGuestOptionUseCase: GetGuestOptionUseCase,
    private val saveGuestOptionUseCase: SaveGuestOptionUseCase,
    getNotificationOptionUseCase: GetNotificationOptionUseCase,
    private val saveNotificationOptionUseCase: SaveNotificationOptionUseCase,
    private val moveGuestDataUseCase: MoveGuestDataUseCase,
    private val moveUserIdGifticonUseCase: MoveUserIdGifticonUseCase
) : ViewModel() {

    val securityOption: StateFlow<SecurityOption> =
        getSecurityOptionUseCase().map {
            SecurityOption.values()[it]
        }.stateIn(viewModelScope, SharingStarted.Eagerly, SecurityOption.NONE)

    private val guestOption: StateFlow<Boolean> =
        getGuestOptionUseCase().stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val notificationOption: StateFlow<Boolean> =
        getNotificationOptionUseCase().stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val userPreferenceState: StateFlow<UserPreferenceState> = combine(
        guestOption,
        securityOption,
        notificationOption
    ) { guest, security, notification ->
        UserPreferenceState(guest, security, notification)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UserPreferenceState())

    fun saveSecurityOption(selectedOption: Int) {
        viewModelScope.launch {
            saveSecurityOptionUseCase(selectedOption)
        }
    }

    fun moveGuestData(uid: String) {
        viewModelScope.launch {
            saveGuestOptionUseCase(false)
            moveGuestDataUseCase(uid)
            moveUserIdGifticonUseCase("Guest", uid)
        }
    }

    fun saveNotificationOption(selectedOption: Boolean) {
        viewModelScope.launch {
            saveNotificationOptionUseCase(selectedOption)
        }
    }
}
