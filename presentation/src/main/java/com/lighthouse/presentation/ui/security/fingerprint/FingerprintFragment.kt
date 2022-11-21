package com.lighthouse.presentation.ui.security.fingerprint

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.FragmentFingerprintBinding
import com.lighthouse.presentation.ui.common.viewBindings
import com.lighthouse.presentation.ui.main.MainActivity
import com.lighthouse.presentation.ui.security.fingerprint.biometric.FingerprintAuthCallback
import com.lighthouse.presentation.ui.security.fingerprint.biometric.FingerprintAuthManager
import timber.log.Timber

class FingerprintFragment : Fragment(R.layout.fragment_fingerprint), FingerprintAuthCallback {

    private val binding by viewBindings(FragmentFingerprintBinding::bind)
    private lateinit var fingerprintAuthManager: FingerprintAuthManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fingerprintAuthManager =
            FingerprintAuthManager(requireActivity(), this)

        binding.btnUseFingerprint.setOnClickListener {
            fingerprintAuthManager.authenticate()
        }

        binding.btnNotUseFingerprint.setOnClickListener {
            gotoMain()
        }
    }

    override fun onBiometricAuthSuccess() {
        Timber.tag("Finger").d("Success")
        gotoMain()
    }

    override fun onBiometricAuthError() {
        Timber.tag("Finger").d("Error")
    }

    override fun onBiometricAuthCancel() {
        Timber.tag("Finger").d("Cancel")
    }

    override fun onMessagePublished(id: Int) {
        Snackbar.make(
            requireView(),
            getString(id),
            Snackbar.LENGTH_SHORT
        ).show()
    }

    override fun onFingerprintRegisterSuccess() {
        fingerprintAuthManager.authenticate()
    }

    override fun onFingerprintRegisterError(result: ActivityResult) {
        Timber.tag("Finger").d("지문 등록 Error $result")
    }

    private fun gotoMain() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
    }
}
