package com.lighthouse.presentation.ui.security

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.FragmentFingerprintBinding
import com.lighthouse.presentation.ui.common.viewBindings
import com.lighthouse.presentation.ui.main.MainActivity
import com.lighthouse.presentation.ui.security.fingerprint.FingerprintAuthCallback
import com.lighthouse.presentation.ui.security.fingerprint.FingerprintAuthManager

class FingerprintFragment : Fragment(), FingerprintAuthCallback {

    private val binding by viewBindings(FragmentFingerprintBinding::bind)
    private lateinit var fingerprintAuthManager: FingerprintAuthManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_fingerprint, container, false)
    }

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
        Log.d("Finger", "Success")
        gotoMain()
    }

    override fun onBiometricAuthError() {
        Log.d("Finger", "Error")
    }

    override fun onBiometricAuthCancel() {
        Log.d("Finger", "Cancel")
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
        Log.d("Finger", "지문 등록 Error $result")
    }

    private fun gotoMain() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
    }
}
