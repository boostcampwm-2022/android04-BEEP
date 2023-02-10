package com.lighthouse.presentation.ui.setting

import com.lighthouse.beep.model.user.SecurityOption

data class UserPreferenceState(
    val guest: Boolean = true,
    val security: SecurityOption = SecurityOption.NONE,
    val notification: Boolean = true
)
