package com.lighthouse.presentation.ui.security

import androidx.fragment.app.FragmentManager
import com.lighthouse.presentation.ui.security.pin.PinDialog
import com.lighthouse.presentation.ui.setting.SecurityOption
import com.lighthouse.presentation.util.UserPreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class AuthManager(
    private val supportFragmentManager: FragmentManager,
    private val authCallback: AuthCallback
) {

    private lateinit var job: Job
    private val pinDialog = PinDialog(authCallback)

    @Inject
    lateinit var userPreference: UserPreference

    fun auth() {
        job = CoroutineScope(Dispatchers.IO).launch {
            // Usecase를 만들어서 Flow<Int>가 아닌 Int로 받아오는 건 어떤가?
            when (userPreference.securityOption.value) {
                SecurityOption.NONE -> authCallback.onAuthSuccess()
                SecurityOption.PIN -> authPin()
                SecurityOption.FINGERPRINT -> authFingerprint()
            }
            Timber.tag("Auth").d("${userPreference.securityOption.value}")
        }
    }

    private fun authPin() {
        pinDialog.show(supportFragmentManager, "PIN")
    }

    private fun authFingerprint() {
    }
}
