package com.lighthouse.presentation.ui.security.fingerprint.biometric

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity

class FingerprintAuthManager(
    private val activity: FragmentActivity,
    private val activityLauncher: ActivityResultLauncher<Intent>,
    private val fingerprintAuthCallback: FingerprintAuthCallback
) {

    private val fingerprintAuth by lazy {
        BiometricAuth(activity, activityLauncher, fingerprintAuthCallback)
    }

    fun authenticate() {
        fingerprintAuth.authenticate()
    }
}
