package com.lighthouse.presentation.ui.security.fingerprint.biometric

import android.hardware.fingerprint.FingerprintManager
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.core.os.CancellationSignal
import androidx.fragment.app.FragmentActivity
import com.lighthouse.domain.usecase.GetFingerprintCipherUseCase
import com.lighthouse.presentation.R
import com.lighthouse.presentation.ui.security.fingerprint.FingerprintBottomSheetDialog
import javax.inject.Inject

class LegacyFingerprintAuth(
    private val activity: FragmentActivity,
    private val fingerprintAuthCallback: FingerprintAuthCallback
) : FingerprintAuth {

    @Inject
    lateinit var getFingerprintCipherUseCase: GetFingerprintCipherUseCase
    private val fingerprintManager = FingerprintManagerCompat.from(activity.applicationContext)
    private val cancellationSignal = CancellationSignal()
    private val fingerprintBottomSheetDialog by lazy {
        FingerprintBottomSheetDialog().also {
            it.setFingerprintAuthCallback(fingerprintAuthCallback)
        }
    }

    private val fingerprintCallback = object : FingerprintManagerCompat.AuthenticationCallback() {
        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            fingerprintBottomSheetDialog.failAuthentication()
        }

        override fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult?) {
            super.onAuthenticationSucceeded(result)
            fingerprintAuthCallback.onBiometricAuthSuccess()
            fingerprintBottomSheetDialog.dismiss()
            fingerprintAuthCallback.onMessagePublished(R.string.fingerprint_authentication_success)
        }

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
            super.onAuthenticationError(errorCode, errString)
            val errorMessageId = when (errorCode) {
                FingerprintManager.FINGERPRINT_ERROR_LOCKOUT -> R.string.fingerprint_error_lockout
                FingerprintManager.FINGERPRINT_ERROR_LOCKOUT_PERMANENT -> R.string.fingerprint_error_lockout_permanent
                else -> R.string.fingerprint_unknown_error
            }

            fingerprintBottomSheetDialog.showErrorMessage(errorMessageId)
            fingerprintAuthCallback.onBiometricAuthError()
            cancellationSignal.cancel()
        }
    }

    override fun authenticate() {
        if (fingerprintManager.isHardwareDetected.not()) {
            fingerprintAuthCallback.onMessagePublished(R.string.fingerprint_error_no_hardware)
        } else if (fingerprintManager.hasEnrolledFingerprints().not()) {
            fingerprintAuthCallback.onMessagePublished(R.string.fingerprint_not_enrolled)
        } else {
            fingerprintManager.authenticate(
                FingerprintManagerCompat.CryptoObject(getFingerprintCipherUseCase()),
                0,
                cancellationSignal,
                fingerprintCallback,
                null
            )
            fingerprintBottomSheetDialog.show(activity.supportFragmentManager, "FingerprintBottomSheetDialog")
        }
    }
}
