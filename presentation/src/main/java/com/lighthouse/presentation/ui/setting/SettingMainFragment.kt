package com.lighthouse.presentation.ui.setting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.FragmentSettingMainBinding
import com.lighthouse.presentation.ui.common.viewBindings
import com.lighthouse.presentation.ui.main.MainViewModel
import com.lighthouse.presentation.ui.security.AuthCallback
import com.lighthouse.presentation.ui.security.AuthManager
import com.lighthouse.presentation.ui.signin.SignInActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingMainFragment : Fragment(R.layout.fragment_setting_main), AuthCallback {

    private val binding: FragmentSettingMainBinding by viewBindings()
    private val viewModel: SettingViewModel by activityViewModels()
    private val activityViewModel: MainViewModel by activityViewModels()

    private val settingSecurityFragment by lazy { SettingSecurityFragment() }

    @Inject
    lateinit var authManager: AuthManager
    private val biometricLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> authenticate()
                else -> onAuthError()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.tvSecurity.setOnClickListener { authenticate() }
        binding.tvSignOut.setOnClickListener { signOut() }
    }

    private fun gotoSecuritySetting() {
        activityViewModel.gotoMenuItem(-1)
        parentFragmentManager.commit {
            add(R.id.fcv_setting, settingSecurityFragment)
        }
    }

    private fun authenticate() {
        authManager.auth(requireActivity(), biometricLauncher, this)
    }

    private fun signOut() {
        Firebase.auth.signOut()
        val intent = Intent(requireContext(), SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onAuthSuccess() {
        gotoSecuritySetting()
    }

    override fun onAuthCancel() {
    }

    override fun onAuthError(StringId: Int?) {
        StringId?.let {
            Snackbar.make(requireView(), getString(it), Snackbar.LENGTH_SHORT).show()
        }
    }
}
