package com.lighthouse.presentation.ui.setting

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.FragmentSettingBinding
import com.lighthouse.presentation.ui.common.viewBindings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : Fragment(R.layout.fragment_setting) {

    private val binding: FragmentSettingBinding by viewBindings()
    private val viewModel: SettingViewModel by activityViewModels()

    private val settingMainFragment = SettingMainFragment()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFragmentManager.commit {
            add(R.id.fcv_setting, settingMainFragment)
        }
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }
}
