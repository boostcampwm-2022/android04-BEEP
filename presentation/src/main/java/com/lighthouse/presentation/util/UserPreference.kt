package com.lighthouse.presentation.util

import com.lighthouse.domain.usecase.setting.GetSecurityOptionUseCase
import com.lighthouse.presentation.ui.setting.SecurityOption
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class UserPreference @Inject constructor(
    getSecurityOptionUseCase: GetSecurityOptionUseCase
) {

    val securityOption: StateFlow<SecurityOption> =
        getSecurityOptionUseCase().map {
            SecurityOption.values()[it]
        }.stateIn(CoroutineScope(Dispatchers.Main), SharingStarted.Eagerly, SecurityOption.NONE)
}
