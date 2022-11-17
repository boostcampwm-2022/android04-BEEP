package com.lighthouse.presentation.ui.security.fingerprint

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import com.lighthouse.presentation.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.R)
class BiometricAuth(
    private val activity: FragmentActivity,
    private val biometricLauncher: ActivityResultLauncher<Intent>,
    private val fingerprintAuthCallback: FingerprintAuthCallback
) : FingerprintAuth {

    private var job: Job? = null
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
            job?.cancel()
            fingerprintAuthCallback.onMessagePublished(R.string.fingerprint_authentication_success)
            fingerprintAuthCallback.onBiometricAuthSuccess()
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            job?.cancel()
            fingerprintAuthCallback.onMessagePublished((R.string.fingerprint_authentication_fail))
        }

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            job?.cancel()
            when (errorCode) {
                BiometricPrompt.ERROR_NO_BIOMETRICS -> goBiometricSetting()
                else -> fingerprintAuthCallback.onBiometricAuthError()
            }
        }
    }

    private val biometricPrompt: BiometricPrompt = BiometricPrompt(activity, authenticationCallback)

    private fun goBiometricSetting() {
        val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
            putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, BiometricManager.Authenticators.BIOMETRIC_STRONG)
        }
        biometricLauncher.launch(enrollIntent)
    }

    override fun authenticate() {
        val biometricManager = BiometricManager.from(activity.applicationContext)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                job = CoroutineScope(Dispatchers.Main).launch {
                    biometricPrompt.authenticate(promptInfo)
                }
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> fingerprintAuthCallback.onMessagePublished(R.string.fingerprint_error_no_hardware)
            else -> fingerprintAuthCallback.onMessagePublished(R.string.fingerprint_unavailable)
        }
    }
}
