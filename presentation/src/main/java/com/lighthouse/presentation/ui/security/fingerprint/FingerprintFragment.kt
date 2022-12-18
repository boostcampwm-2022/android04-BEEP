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
import com.lighthouse.presentation.ui.security.AuthCallback
import com.lighthouse.presentation.ui.security.SecurityViewModel
import com.lighthouse.presentation.ui.security.event.SecurityDirections
import com.lighthouse.presentation.ui.security.fingerprint.biometric.BiometricAuth
import com.lighthouse.presentation.ui.setting.SecurityOption

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
            activityViewModel.gotoOtherScreen(SecurityDirections.LOCATION)
        }
    }

    override fun onAuthSuccess() {
        activityViewModel.setSecurityOption(SecurityOption.FINGERPRINT)
        activityViewModel.gotoOtherScreen(SecurityDirections.LOCATION)
    }

    override fun onAuthCancel() {
        activityViewModel.setSecurityOption(SecurityOption.PIN)
    }

    override fun onAuthError(@StringRes stringId: Int?) {
        activityViewModel.setSecurityOption(SecurityOption.PIN)
    }
}
