package com.lighthouse.presentation.ui.security

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.lighthouse.presentation.ui.security.fingerprint.biometric.BiometricAuth
import com.lighthouse.presentation.ui.security.pin.PinDialog
import com.lighthouse.presentation.ui.setting.SecurityOption
import com.lighthouse.presentation.util.UserPreference
import javax.inject.Inject

class AuthManager @Inject constructor(
    private val userPreference: UserPreference
) {

    fun auth(
        activity: FragmentActivity,
        biometricLauncher: ActivityResultLauncher<Intent>,
        authCallback: AuthCallback
    ) {
        when (userPreference.securityOption.value) {
            SecurityOption.NONE -> authCallback.onAuthSuccess()
            SecurityOption.PIN -> authPin(activity.supportFragmentManager, authCallback)
            SecurityOption.FINGERPRINT -> authFingerprint(activity, biometricLauncher, authCallback)
        }
    }

    private fun authPin(supportFragmentManager: FragmentManager, authCallback: AuthCallback) {
        PinDialog(authCallback).show(supportFragmentManager, PIN_TAG)
    }

    fun authFingerprint(
        activity: FragmentActivity,
        biometricLauncher: ActivityResultLauncher<Intent>,
        authCallback: AuthCallback
    ) {
        BiometricAuth(activity, biometricLauncher, authCallback).authenticate()
    }

    companion object {
        private const val PIN_TAG = "PIN"
    }
}
