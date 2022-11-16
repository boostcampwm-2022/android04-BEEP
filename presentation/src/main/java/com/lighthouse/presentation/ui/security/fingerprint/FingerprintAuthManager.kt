package com.lighthouse.presentation.ui.security.fingerprint

import android.app.Activity
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity

class FingerprintAuthManager(
    private val activity: FragmentActivity,
    private val fingerprintAuthCallback: FingerprintAuthCallback
) {

    private val activityLauncher =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> fingerprintAuthCallback.onFingerprintRegisterSuccess()
                else -> fingerprintAuthCallback.onFingerprintRegisterError(result)
            }
        }

    private val fingerprintAuth by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            BiometricAuth(activity, activityLauncher, fingerprintAuthCallback)
        } else {
            LegacyFingerprintAuth(activity, fingerprintAuthCallback)
        }
    }

    fun authenticate() {
        fingerprintAuth.authenticate()
    }
}
