package com.lighthouse.presentation.ui.setting

import android.content.Context
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.FragmentSecuritySettingBinding
import com.lighthouse.presentation.ui.common.viewBindings
import com.lighthouse.presentation.ui.main.MainViewModel

class SecuritySettingFragment : Fragment(R.layout.fragment_security_setting) {

    private val binding: FragmentSecuritySettingBinding by viewBindings()
    private val activityViewModel: MainViewModel by activityViewModels()

    private lateinit var callback: OnBackPressedCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)

        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activityViewModel.gotoMenuItem(R.id.menu_setting)
                val currFragment = parentFragmentManager.findFragmentByTag("SSF")
                currFragment?.let {
                    parentFragmentManager.commit { hide(it) }
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
}
