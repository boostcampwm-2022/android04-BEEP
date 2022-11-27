package com.lighthouse.presentation.util

import com.lighthouse.domain.model.UserPreferenceOption
import com.lighthouse.domain.repository.UserPreferencesRepository
import com.lighthouse.presentation.ui.setting.SecurityOption
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserPreference @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {

    lateinit var securityOption: StateFlow<SecurityOption>

    init {
        CoroutineScope(Dispatchers.IO).launch {
            securityOption = userPreferencesRepository.getIntOption(UserPreferenceOption.SECURITY).map {
                SecurityOption.values()[it]
            }.stateIn(CoroutineScope(Dispatchers.IO))
        }
    }
}
