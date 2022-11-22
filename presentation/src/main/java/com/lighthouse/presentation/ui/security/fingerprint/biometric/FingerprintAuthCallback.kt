package com.lighthouse.presentation.ui.security.fingerprint.biometric

import androidx.activity.result.ActivityResult
import androidx.annotation.StringRes

interface FingerprintAuthCallback {

    fun onBiometricAuthSuccess()
    fun onBiometricAuthError()
    fun onBiometricAuthCancel()
    fun onMessagePublished(@StringRes id: Int)
    fun onFingerprintRegisterSuccess()
    fun onFingerprintRegisterError(result: ActivityResult)
}
