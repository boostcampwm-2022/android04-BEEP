package com.lighthouse.presentation.ui.setting

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.FragmentSecuritySettingBinding
import com.lighthouse.presentation.ui.common.viewBindings
import com.lighthouse.presentation.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingSecurityFragment : Fragment(R.layout.fragment_security_setting) {

    private val binding: FragmentSecuritySettingBinding by viewBindings()
    private val activityViewModel: MainViewModel by activityViewModels()
    private val viewModel: SettingSecurityViewModel by viewModels()

    private lateinit var callback: OnBackPressedCallback

    private lateinit var securityOptionItems: Array<CharSequence>
    private var checkedSecurityOption: Int = 0
    private lateinit var securityOptionDialog: MaterialAlertDialogBuilder

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        securityOptionItems =
            arrayOf(SecurityOption.NONE.text, SecurityOption.PIN.text, SecurityOption.FINGERPRINT.text)
        checkedSecurityOption = viewModel.securityOptionFlow.value.ordinal
        securityOptionDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("보안 인증 옵션")
            .setNeutralButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("확인") { dialog, which ->
                // TODO: which 저장
                dialog.dismiss()
            }.setSingleChoiceItems(securityOptionItems, checkedSecurityOption) { dialog, which ->
                checkedSecurityOption = which
            }

        // TODO : repeatOn
        lifecycleScope.launch {
            viewModel.securityOptionEventFlow.collect {
                securityOptionDialog.show()
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
}
