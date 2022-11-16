package com.lighthouse.presentation.ui.security.fingerprint

interface FingerprintAuthCallback {
    fun onBiometricAuthSuccess()
    fun onBiometricAuthError()
    fun onMessagePublished(id: Int)
}
