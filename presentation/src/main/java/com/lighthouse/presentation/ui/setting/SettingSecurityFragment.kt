package com.lighthouse.presentation.ui.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.FragmentSecuritySettingBinding
import com.lighthouse.presentation.ui.common.viewBindings
import com.lighthouse.presentation.ui.main.MainViewModel
import com.lighthouse.presentation.ui.security.SecurityActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingSecurityFragment : Fragment(R.layout.fragment_security_setting) {

    private val binding: FragmentSecuritySettingBinding by viewBindings()
    private val activityViewModel: MainViewModel by activityViewModels()
    private val viewModel: SettingSecurityViewModel by viewModels()

    private val securityOptionItems =
        arrayOf(SecurityOption.NONE.text, SecurityOption.PIN.text, SecurityOption.FINGERPRINT.text)
    private var checkedSecurityOption: Int = 0
    private var currentSecurityOption: Int = 0

    private lateinit var callback: OnBackPressedCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activityViewModel.gotoMenuItem(R.id.menu_setting)

                parentFragmentManager.commit {
                    replace(R.id.fcv_setting, SettingMainFragment(), "SMF")
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        checkedSecurityOption = viewModel.securityOptionFlow.value.ordinal
        currentSecurityOption = checkedSecurityOption

        binding.tvChangeSecurityOption.setOnClickListener {
            // 옵션 변경 시 currentSecurityOption 바뀌기 때문에 매번 AlertDialog 을 만들어 사용합니다.
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("보안 인증 옵션")
                .setNeutralButton("취소") { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton("확인") { dialog, which ->
                    if (checkedSecurityOption != currentSecurityOption) {
                        viewModel.saveSecurityOption(checkedSecurityOption)
                        when (checkedSecurityOption) {
                            1 -> gotoPinSetting()
                        }
                    }
                    dialog.dismiss()
                }.setSingleChoiceItems(securityOptionItems, currentSecurityOption) { dialog, which ->
                    checkedSecurityOption = which
                }.show()
        }
    }

    private fun gotoPinSetting() {
        val intent = Intent(requireContext(), SecurityActivity::class.java)
        intent.putExtra("revise", true)
        startActivity(intent)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
}
