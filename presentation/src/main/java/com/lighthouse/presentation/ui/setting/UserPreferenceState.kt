package com.lighthouse.presentation.ui.setting

data class UserPreferenceState(
    val guest: Boolean = true,
    val security: SecurityOption = SecurityOption.NONE
)
