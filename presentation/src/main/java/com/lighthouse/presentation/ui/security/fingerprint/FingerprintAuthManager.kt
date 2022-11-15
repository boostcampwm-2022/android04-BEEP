package com.lighthouse.presentation.ui.security.fingerprint

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment

class FingerprintAuthManager(
    private val fragment: Fragment,
    private val context: Context,
    private val biometricLauncher: ActivityResultLauncher<Intent>?,
    private val fingerprintAuthCallback: FingerprintAuthCallback
) {
    private val fingerprintAuth = initFingerPrintAuth()

    fun authenticate() {
        fingerprintAuth.authenticate()
    }

    private fun initFingerPrintAuth(): FingerprintAuth {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (biometricLauncher == null) {
                throw Exception("FingerPrint init Error")
            }
            BiometricAuth(fragment, context, biometricLauncher, fingerprintAuthCallback)
        } else {
            LegacyFingerprintAuth(fragment, context, fingerprintAuthCallback)
        }
    }
}
