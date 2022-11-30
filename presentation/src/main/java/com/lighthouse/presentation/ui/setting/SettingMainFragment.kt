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
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.FragmentSettingMainBinding
import com.lighthouse.presentation.ui.common.viewBindings
import com.lighthouse.presentation.ui.main.MainViewModel
import com.lighthouse.presentation.ui.security.AuthCallback
import com.lighthouse.presentation.ui.security.AuthManager
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
    }

    private fun gotoSecuritySetting() {
        activityViewModel.gotoMenuItem(-1)
        parentFragmentManager.commit {
            add(R.id.fcv_setting, settingSecurityFragment, "SSF")
        }
    }

    private fun authenticate() {
        authManager.auth(requireActivity(), biometricLauncher, this)
    }

    override fun onAuthSuccess() {
        gotoSecuritySetting()
    }

    override fun onAuthCancel() {
        TODO("Not yet implemented")
    }

    override fun onAuthError(StringId: Int?) {
        TODO("Not yet implemented")
    }
}
