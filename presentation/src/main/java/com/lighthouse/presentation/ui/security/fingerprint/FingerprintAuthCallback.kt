package com.lighthouse.presentation.ui.security.fingerprint

import androidx.activity.result.ActivityResult

interface FingerprintAuthCallback {

    fun onBiometricAuthSuccess()
    fun onBiometricAuthError()
    fun onMessagePublished(id: Int)
    fun onFingerprintRegisterSuccess()
    fun onFingerprintRegisterError(result: ActivityResult)
}
