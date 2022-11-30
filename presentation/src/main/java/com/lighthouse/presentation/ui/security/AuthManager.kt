package com.lighthouse.presentation.ui.security

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.lighthouse.presentation.ui.security.fingerprint.biometric.BiometricAuth
import com.lighthouse.presentation.ui.security.pin.PinDialog
import com.lighthouse.presentation.ui.setting.SecurityOption
import com.lighthouse.presentation.util.UserPreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthManager @Inject constructor(
    private val userPreference: UserPreference
) {
    private lateinit var job: Job
    private lateinit var pinDialog: PinDialog
    private lateinit var biometricAuth: BiometricAuth

    fun auth(
        activity: FragmentActivity,
        biometricLauncher: ActivityResultLauncher<Intent>,
        authCallback: AuthCallback
    ) {
        job = CoroutineScope(Dispatchers.Main).launch {
            when (userPreference.securityOption.value) {
                SecurityOption.NONE -> authCallback.onAuthSuccess()
                SecurityOption.PIN -> authPin(activity.supportFragmentManager, authCallback)
                SecurityOption.FINGERPRINT -> authFingerprint(activity, biometricLauncher, authCallback)
            }
        }
    }

    private fun authPin(supportFragmentManager: FragmentManager, authCallback: AuthCallback) {
        if (::pinDialog.isInitialized.not()) {
            pinDialog = PinDialog(authCallback)
        }
        pinDialog.show(supportFragmentManager, PIN_TAG)
        // PinDialog(authCallback).show(supportFragmentManager, PIN_TAG)
    }

    private fun authFingerprint(
        activity: FragmentActivity,
        biometricLauncher: ActivityResultLauncher<Intent>,
        authCallback: AuthCallback
    ) {
        if (::biometricAuth.isInitialized.not()) {
            biometricAuth = BiometricAuth(activity, biometricLauncher, authCallback)
        }
        biometricAuth.authenticate()
    }

    companion object {
        private const val PIN_TAG = "PIN"
    }
}
