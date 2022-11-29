package com.lighthouse.presentation.ui.security.fingerprint

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import com.lighthouse.presentation.R
import com.lighthouse.presentation.ui.security.AuthCallback

class BiometricAuth(
    private val activity: FragmentActivity,
    private val biometricLauncher: ActivityResultLauncher<Intent>,
    private val authCallback: AuthCallback
) {

    private val promptInfo: BiometricPrompt.PromptInfo by lazy {
        BiometricPrompt.PromptInfo.Builder().apply {
            setTitle(activity.getString(R.string.fingerprint_authentication))
            setDescription(activity.getString(R.string.fingerprint_description))
            setNegativeButtonText(activity.getString(R.string.all_cancel))
        }.build()
    }

    private val authenticationCallback = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            authCallback.onAuthSuccess()
        }

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            when (errorCode) {
                BiometricPrompt.ERROR_NO_BIOMETRICS -> goBiometricSetting()
                BiometricPrompt.ERROR_NEGATIVE_BUTTON -> authCallback.onAuthCancel()
                else -> authCallback.onAuthError(R.string.fingerprint_unknown_error)
            }
        }
    }

    private val biometricPrompt: BiometricPrompt = BiometricPrompt(activity, authenticationCallback)

    private fun goBiometricSetting() {
        val enrollIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                putExtra(
                    Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                    BiometricManager.Authenticators.BIOMETRIC_STRONG
                )
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Intent(Settings.ACTION_FINGERPRINT_ENROLL)
        } else {
            Intent(Settings.ACTION_SECURITY_SETTINGS)
        }
        biometricLauncher.launch(enrollIntent)
    }

    fun authenticate() {
        val biometricManager = BiometricManager.from(activity.applicationContext)
        val biometricAvailable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
        } else {
            biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)
        }

        when (biometricAvailable) {
            BiometricManager.BIOMETRIC_SUCCESS -> biometricPrompt.authenticate(promptInfo)
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> goBiometricSetting()
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> authCallback.onAuthError(R.string.fingerprint_error_no_hardware)
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> authCallback.onAuthError(R.string.fingerprint_error_no_hardware)
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> authCallback.onAuthError(R.string.fingerprint_unavailable)
            else -> authCallback.onAuthError(R.string.fingerprint_unknown_error)
        }
    }
}
