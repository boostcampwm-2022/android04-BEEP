package com.lighthouse.presentation.ui.security.fingerprint

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.lighthouse.presentation.R

class BiometricAuth(
    private val fragment: Fragment,
    private val context: Context,
    private val biometricLauncher: ActivityResultLauncher<Intent>,
    private val fingerprintAuthCallback: FingerprintAuthCallback
) : FingerprintAuth {
    private lateinit var biometricPrompt: BiometricPrompt
    private val promptInfo: BiometricPrompt.PromptInfo

    init {
        promptInfo = setPromptInfo()
        setBiometricPrompt()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun goBiometricSetting() {
        val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
            putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, BiometricManager.Authenticators.BIOMETRIC_STRONG)
        }
        biometricLauncher.launch(enrollIntent)
    }

    private fun setPromptInfo(): BiometricPrompt.PromptInfo = BiometricPrompt.PromptInfo.Builder().apply {
        setTitle(context.getString(R.string.fingerprint_authentication))
        setDescription(context.getString(R.string.fingerprint_description))
        setNegativeButtonText(context.getString(R.string.all_cancel))
    }.build()

    private fun setBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(context)
        biometricPrompt = BiometricPrompt(
            fragment,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    fingerprintAuthCallback.onMessagePublished(R.string.fingerprint_authentication_success)
                    fingerprintAuthCallback.onBiometricAuthSuccess()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    fingerprintAuthCallback.onMessagePublished((R.string.fingerprint_authentication_fail))
                }

                @RequiresApi(Build.VERSION_CODES.R)
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    when (errorCode) {
                        BiometricPrompt.ERROR_NO_BIOMETRICS -> goBiometricSetting()
                        else -> fingerprintAuthCallback.onBiometricAuthError()
                    }
                }
            }
        )
    }

    override fun authenticate() {
        val biometricManager = BiometricManager.from(context)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> biometricPrompt.authenticate(promptInfo)
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> fingerprintAuthCallback.onMessagePublished((R.string.fingerprint_error_no_hardware))
            else -> fingerprintAuthCallback.onMessagePublished((R.string.fingerprint_unavailable))
        }
    }
}
