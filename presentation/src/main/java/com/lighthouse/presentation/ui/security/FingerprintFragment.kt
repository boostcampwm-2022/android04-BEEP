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
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.lighthouse.presentation.R
import com.lighthouse.presentation.ui.security.fingerprint.FingerprintAuthCallback
import com.lighthouse.presentation.ui.security.fingerprint.FingerprintAuthManager

class FingerprintFragment : Fragment(), FingerprintAuthCallback {

    private lateinit var fingerprintAuthManager: FingerprintAuthManager
    private var biometricLauncher: ActivityResultLauncher<Intent>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_fingerprint, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            biometricLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                when (result.resultCode) {
                    Activity.RESULT_OK -> {
                        fingerprintAuthManager.authenticate()
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
        }

        fingerprintAuthManager =
            FingerprintAuthManager(requireActivity(), requireContext(), biometricLauncher, this)
        fingerprintAuthManager.authenticate()
    }

    override fun onBiometricAuthSuccess() {
        Log.d("Finger", "Success")
    }

    override fun onBiometricAuthError() {
        Log.d("Finger", "Error")
    }

    override fun onMessagePublished(id: Int) {
        Snackbar.make(
            requireView(),
            getString(id),
            Snackbar.LENGTH_SHORT
        ).show()
    }
}
