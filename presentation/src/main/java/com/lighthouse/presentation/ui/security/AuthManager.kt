package com.lighthouse.presentation.ui.security

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.lighthouse.domain.usecase.setting.GetSecurityOptionUseCase
import com.lighthouse.presentation.ui.security.fingerprint.biometric.BiometricAuth
import com.lighthouse.presentation.ui.security.pin.PinDialog
import com.lighthouse.presentation.ui.setting.SecurityOption
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class AuthManager @Inject constructor(
    getSecurityOptionUseCase: GetSecurityOptionUseCase
) {

    private val securityOption: StateFlow<SecurityOption> =
        getSecurityOptionUseCase().map {
            SecurityOption.values()[it]
        }.stateIn(CoroutineScope(Dispatchers.IO), SharingStarted.Eagerly, SecurityOption.NONE)

    fun auth(
        activity: FragmentActivity,
        biometricLauncher: ActivityResultLauncher<Intent>,
        authCallback: AuthCallback
    ) {
        when (securityOption.value) {
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
