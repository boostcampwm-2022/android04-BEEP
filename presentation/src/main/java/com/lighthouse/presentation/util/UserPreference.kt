package com.lighthouse.presentation.util

import com.lighthouse.domain.model.UserPreferenceOption
import com.lighthouse.domain.repository.UserPreferencesRepository
import com.lighthouse.presentation.ui.setting.SecurityOption
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class UserPreference @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository
) {

    val securityOption: StateFlow<SecurityOption> =
        userPreferencesRepository.getIntOption(UserPreferenceOption.SECURITY).map {
            SecurityOption.values()[it]
        }.stateIn(CoroutineScope(Dispatchers.Main), SharingStarted.Eagerly, SecurityOption.NONE)
}
