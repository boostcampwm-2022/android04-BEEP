package com.lighthouse.presentation.ui.security

import android.content.Context
import android.hardware.fingerprint.FingerprintManager
import android.os.CancellationSignal
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.lighthouse.presentation.R

class FingerprintAuthManager(
    private val fragment: Fragment,
    private val context: Context
) {
    private val fingerprintManager = context.getSystemService(FingerprintManager::class.java)
    private val cryptoObjectHelper = CryptoObjectHelper()
    private val cancellationSignal = CancellationSignal()
    private val fingerprintCallback = object : FingerprintManager.AuthenticationCallback() {
        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            cancellationSignal.cancel()
            Log.d("Finger", "fail")
        }

        override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult?) {
            super.onAuthenticationSucceeded(result)
            Log.d("Finger", "성공 $result")
        }

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
            super.onAuthenticationError(errorCode, errString)
            cancellationSignal.cancel()
            Log.d("Finger", "error")
        }
    }

    fun examine() {
        if (fingerprintManager.isHardwareDetected.not()) {
            printSnackBar(R.string.fingerprint_error_no_hardware)
        } else if (fingerprintManager.hasEnrolledFingerprints().not()) {
            printSnackBar(R.string.fingerprint_not_enrolled)
        } else {
            fingerprintManager.authenticate(
                cryptoObjectHelper.getCryptoObject(),
                cancellationSignal,
                0,
                fingerprintCallback,
                null
            )
        }
    }

    private fun printSnackBar(id: Int) {
        Snackbar.make(fragment.requireView(), context.getString(id), Snackbar.LENGTH_SHORT).show()
    }

    private fun printSnackBar(msg: String) {
        Snackbar.make(fragment.requireView(), msg, Snackbar.LENGTH_SHORT).show()
    }
}
