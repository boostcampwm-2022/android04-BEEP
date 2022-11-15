package com.lighthouse.presentation.ui.security

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.lighthouse.presentation.R

class FingerprintFragment : Fragment(), BiometricAuthCallback {

    private lateinit var biometricAuthManager: BiometricAuthManager

    @RequiresApi(Build.VERSION_CODES.R)
    private val biometricLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    biometricAuthManager.examineBiometricAuthenticate()
                }
                else -> {
                    Snackbar.make(
                        requireView(),
                        getString(R.string.fingerprint_info_obtain_fail),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_fingerprint, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        biometricAuthManager = BiometricAuthManager(this, requireContext(), biometricLauncher, this)
        biometricAuthManager.examineBiometricAuthenticate()
    }

    override fun onBiometricAuthSuccess() {
        Log.d("Bio", "Success~~~~~")
    }

    override fun onBiometricAuthError() {
        Log.d("Bio", "error~~~~~~~")
    }
}
