package com.lighthouse.presentation.ui.setting

import android.os.Bundle
import android.view.View
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
import timber.log.Timber

class SettingFragment : Fragment(R.layout.fragment_setting) {

    private val binding: FragmentSettingBinding by viewBindings()
    private val viewModel: SettingViewModel by viewModels()
    private val activityViewModel: MainViewModel by activityViewModels()

    private val securitySettingFragment by lazy { SecuritySettingFragment() }

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
            UserPreferenceOption.SECURITY -> gotoSecuritySetting()
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
                add(R.id.fl_container, securitySettingFragment, "SSF")
                addToBackStack(null)
            }
        } else {
            parentFragmentManager.commit {
                show(currFragment)
            }
        }
    }
}
