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
import androidx.fragment.app.viewModels
import com.lighthouse.domain.model.UserPreferenceOption
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.FragmentSettingBinding
import com.lighthouse.presentation.extension.repeatOnStarted
import com.lighthouse.presentation.ui.common.viewBindings
import com.lighthouse.presentation.ui.main.MainViewModel
import com.lighthouse.presentation.ui.security.AuthCallback
import com.lighthouse.presentation.ui.security.AuthManager
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment : Fragment(R.layout.fragment_setting), AuthCallback {

    private val binding: FragmentSettingBinding by viewBindings()
    private val viewModel: SettingViewModel by viewModels()
    private val activityViewModel: MainViewModel by activityViewModels()

    private val securitySettingFragment by lazy { SecuritySettingFragment() }

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

        repeatOnStarted {
            viewModel.optionFlow.collect { option ->
                enterOptionSetting(option)
            }
        }
    }

    private fun enterOptionSetting(option: UserPreferenceOption) {
        when (option) {
            UserPreferenceOption.SECURITY -> authenticate()
            UserPreferenceOption.NOTIFICATION -> TODO()
            UserPreferenceOption.LOCATION -> TODO()
        }
    }

    private fun gotoSecuritySetting() {
        Timber.tag("SETTING").d("gotoSecuritySetting")
        activityViewModel.gotoMenuItem(-1)

        val currFragment = parentFragmentManager.findFragmentByTag("SSF")
        if (currFragment == null) {
            parentFragmentManager.commit {
                replace(R.id.fl_container, securitySettingFragment, "SSF")
                addToBackStack(null)
            }
        } else {
            parentFragmentManager.commit {
                show(currFragment)
            }
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
