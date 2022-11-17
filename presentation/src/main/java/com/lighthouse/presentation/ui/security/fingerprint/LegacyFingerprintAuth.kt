package com.lighthouse.presentation.ui.security.fingerprint

import android.util.Log
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.core.os.CancellationSignal
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.lighthouse.presentation.R

class LegacyFingerprintAuth(
    private val activity: FragmentActivity,
    private val fingerprintAuthCallback: FingerprintAuthCallback
) : FingerprintAuth {

    private val fingerprintManager = FingerprintManagerCompat.from(activity.applicationContext)
    private val cryptoObjectHelper = CryptoObjectHelper()
    private val cancellationSignal = CancellationSignal()

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
            BottomSheetDialog(activity).show()
        }
    }
}
