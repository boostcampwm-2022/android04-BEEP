package com.lighthouse.presentation.ui.security.fingerprint

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity

class FingerprintAuthManager(
    private val activity: FragmentActivity,
    private val context: Context,
    private val biometricLauncher: ActivityResultLauncher<Intent>?,
    private val fingerprintAuthCallback: FingerprintAuthCallback
) {

    private val fingerprintAuth by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && biometricLauncher != null) {
            BiometricAuth(activity, context, biometricLauncher, fingerprintAuthCallback)
        } else {
            LegacyFingerprintAuth(context, fingerprintAuthCallback)
        }
    }

    fun authenticate() {
        fingerprintAuth.authenticate()
    }
}
