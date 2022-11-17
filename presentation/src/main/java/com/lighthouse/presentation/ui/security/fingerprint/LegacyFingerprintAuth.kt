package com.lighthouse.presentation.ui.security.fingerprint

import android.util.Log
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.core.os.CancellationSignal
import androidx.fragment.app.FragmentActivity
import com.lighthouse.presentation.R
import com.lighthouse.presentation.ui.security.FingerprintBottomSheetDialog

class LegacyFingerprintAuth(
    private val activity: FragmentActivity,
    private val fingerprintAuthCallback: FingerprintAuthCallback
) : FingerprintAuth {

    private val fingerprintManager = FingerprintManagerCompat.from(activity.applicationContext)
    private val cryptoObjectHelper = CryptoObjectHelper()
    private val cancellationSignal = CancellationSignal()
    private val fingerprintBottomSheetDialog = FingerprintBottomSheetDialog()

    private val fingerprintCallback = object : FingerprintManagerCompat.AuthenticationCallback() {
        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            cancellationSignal.cancel()
            Log.d("Finger", "onAuthenticationFailed")
            // TODO: 재시도
        }

        override fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult?) {
            super.onAuthenticationSucceeded(result)
            Log.d("Finger", "onAuthenticationSucceeded")
            fingerprintAuthCallback.onBiometricAuthSuccess()
            fingerprintBottomSheetDialog.dismiss()
            fingerprintAuthCallback.onMessagePublished(R.string.fingerprint_authentication_success)
        }

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
            super.onAuthenticationError(errorCode, errString)
            Log.d("Finger", "onAuthenticationError $errString")
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
                cryptoObjectHelper.getCryptoObject(),
                0,
                cancellationSignal,
                fingerprintCallback,
                null
            )
            fingerprintBottomSheetDialog.show(activity.supportFragmentManager, "FingerprintBottomSheetDialog")
        }
    }
}
