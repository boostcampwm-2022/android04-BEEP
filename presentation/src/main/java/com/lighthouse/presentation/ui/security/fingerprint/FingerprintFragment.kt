package com.lighthouse.presentation.ui.security.fingerprint

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.FragmentFingerprintBinding
import com.lighthouse.presentation.ui.common.viewBindings
import com.lighthouse.presentation.ui.main.MainActivity
import com.lighthouse.presentation.ui.security.AuthCallback
import com.lighthouse.presentation.ui.security.SecurityViewModel
import com.lighthouse.presentation.ui.security.fingerprint.biometric.BiometricAuth
import com.lighthouse.presentation.ui.setting.SecurityOption
import timber.log.Timber

class FingerprintFragment : Fragment(R.layout.fragment_fingerprint), AuthCallback {

    private val activityViewModel: SecurityViewModel by activityViewModels()
    private lateinit var biometricAuth: BiometricAuth
    private val binding: FragmentFingerprintBinding by viewBindings()

    private val activityLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> onAuthSuccess()
                else -> onAuthError()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        biometricAuth =
            BiometricAuth(requireActivity(), activityLauncher, this)

        binding.btnUseFingerprint.setOnClickListener {
            biometricAuth.authenticate()
        }

        binding.btnNotUseFingerprint.setOnClickListener {
            gotoMain()
        }
    }

    private fun gotoMain() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onAuthSuccess() {
        Timber.tag("Finger").d("Success")
        activityViewModel.setSecurityOption(SecurityOption.FINGERPRINT)
        gotoMain()
    }

    override fun onAuthCancel() {
        Timber.tag("Finger").d("Cancel")
        activityViewModel.setSecurityOption(SecurityOption.PIN)
    }

    override fun onAuthError(@StringRes StringId: Int?) {
        Timber.tag("Finger").d("Error")
        activityViewModel.setSecurityOption(SecurityOption.PIN)
    }
}
