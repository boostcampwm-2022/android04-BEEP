package com.lighthouse.presentation.ui.security

interface BiometricAuthCallback {
    fun onBiometricAuthSuccess()
    fun onBiometricAuthError()
}
