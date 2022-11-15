package com.lighthouse.presentation.ui.security

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
import com.google.android.material.snackbar.Snackbar
import com.lighthouse.presentation.R

class BiometricAuthManager(
    private val fragment: Fragment,
    private val context: Context,
    private val biometricLauncher: ActivityResultLauncher<Intent>,
    private val biometricAuthCallback: BiometricAuthCallback
) {
    private lateinit var biometricPrompt: BiometricPrompt
    private val promptInfo: BiometricPrompt.PromptInfo

    init {
        promptInfo = setPromptInfo()
        setBiometricPrompt()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun examineBiometricAuthenticate() {
        val biometricManager = BiometricManager.from(context)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> biometricPrompt.authenticate(promptInfo)
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> printSnackBar(R.string.fingerprint_error_no_hardware)
            else -> printSnackBar(R.string.fingerprint_unavailable)
        }
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
                    printSnackBar(R.string.fingerprint_authentication_success)
                    biometricAuthCallback.onBiometricAuthSuccess()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    printSnackBar(R.string.fingerprint_authentication_fail)
                }

                @RequiresApi(Build.VERSION_CODES.R)
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    printSnackBar(errString.toString())
                    when (errorCode) {
                        BiometricPrompt.ERROR_NO_BIOMETRICS -> goBiometricSetting()
                        else -> biometricAuthCallback.onBiometricAuthError()
                    }
                }
            }
        )
    }

    private fun printSnackBar(id: Int) {
        Snackbar.make(fragment.requireView(), context.getString(id), Snackbar.LENGTH_SHORT).show()
    }

    private fun printSnackBar(msg: String) {
        Snackbar.make(fragment.requireView(), msg, Snackbar.LENGTH_SHORT).show()
    }
}
