package com.lighthouse.presentation.ui.security

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.lighthouse.presentation.R

class FingerprintFragment : Fragment() {

    private lateinit var biometricPrompt: BiometricPrompt
    private val promptInfo: BiometricPrompt.PromptInfo = setPromptInfo()

    @RequiresApi(Build.VERSION_CODES.R)
    private val biometricLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    examineBiometricAuthenticate()
                }
                else -> {
                    Snackbar.make(
                        requireView(),
                        getString(R.string.fingerprint_info_obtain_fail),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_fingerprint, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setBiometricPrompt()
        examineBiometricAuthenticate()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun examineBiometricAuthenticate() {
        val biometricManager = BiometricManager.from(requireContext())
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> biometricPrompt.authenticate(promptInfo)
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                Snackbar.make(requireView(), getString(R.string.fingerprint_error_no_hardware), Snackbar.LENGTH_SHORT)
                    .show()
            else ->
                Snackbar.make(requireView(), getString(R.string.fingerprint_unavailable), Snackbar.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun goBiometricSetting() {
        val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
            putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, BIOMETRIC_STRONG)
        }
        biometricLauncher.launch(enrollIntent)
    }

    private fun setPromptInfo(): BiometricPrompt.PromptInfo = BiometricPrompt.PromptInfo.Builder().apply {
        setTitle(getString(R.string.fingerprint_authentication))
        setDescription(getString(R.string.fingerprint_description))
        setNegativeButtonText(getString(R.string.all_cancel))
    }.build()

    private fun setBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(requireContext())
        biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Snackbar.make(
                        requireView(),
                        getString(R.string.fingerprint_authentication_success),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Snackbar.make(
                        requireView(),
                        getString(R.string.fingerprint_authentication_fail),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

                @RequiresApi(Build.VERSION_CODES.R)
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Snackbar.make(requireView(), "$errorCode $errString", Snackbar.LENGTH_SHORT).show()
                    when (errorCode) {
                        BiometricPrompt.ERROR_NO_BIOMETRICS -> goBiometricSetting()
                    }
                }
            }
        )
    }
}
