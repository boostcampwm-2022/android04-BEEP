package com.lighthouse.presentation.ui.security

import androidx.annotation.StringRes

interface AuthCallback {
    fun onAuthSuccess()
    fun onAuthCancel()
    fun onAuthError(@StringRes stringId: Int? = null)
}
