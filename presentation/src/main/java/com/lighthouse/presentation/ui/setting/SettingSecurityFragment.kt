package com.lighthouse.presentation.ui.setting

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.FragmentSecuritySettingBinding
import com.lighthouse.presentation.ui.common.viewBindings
import com.lighthouse.presentation.ui.main.MainViewModel
import com.lighthouse.presentation.ui.security.SecurityActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@AndroidEntryPoint
class SettingSecurityFragment : Fragment(R.layout.fragment_security_setting) {

    private val binding: FragmentSecuritySettingBinding by viewBindings()
    private val activityViewModel: MainViewModel by activityViewModels()
    private val viewModel: SettingSecurityViewModel by viewModels()

    private val securityOptionItems =
        arrayOf(SecurityOption.NONE.text, SecurityOption.PIN.text, SecurityOption.FINGERPRINT.text)
    private var checkedSecurityOption: Int = 0
    private lateinit var currentSecurityOption: StateFlow<Int>

    private lateinit var callback: OnBackPressedCallback

    private lateinit var optionChangeLauncher: ActivityResultLauncher<Intent>
    private lateinit var pinChangeLauncher: ActivityResultLauncher<Intent>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activityViewModel.gotoMenuItem(R.id.menu_setting)

                parentFragmentManager.commit {
                    remove(this@SettingSecurityFragment)
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

        optionChangeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Snackbar.make(requireView(), "보안 설정을 변경했습니다.", Snackbar.LENGTH_SHORT).show()
                viewModel.saveSecurityOption(checkedSecurityOption)
            } else {
                Snackbar.make(requireView(), "보안 설정 변경에 실패했습니다.", Snackbar.LENGTH_SHORT).show()
            }
        }

        pinChangeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Snackbar.make(requireView(), "보안 설정을 변경했습니다.", Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(requireView(), "보안 설정 변경에 실패했습니다.", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        checkedSecurityOption = viewModel.securityOptionFlow.value.ordinal
        currentSecurityOption = viewModel.securityOptionFlow.map { it.ordinal }
            .stateIn(lifecycleScope, SharingStarted.Eagerly, checkedSecurityOption)

        binding.tvChangeSecurityOption.setOnClickListener {
            // 옵션 변경 시 currentSecurityOption 바뀌기 때문에 매번 AlertDialog 을 만들어 사용합니다.
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("보안 인증 옵션")
                .setNeutralButton("취소") { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton("확인") { dialog, which ->
                    if (checkedSecurityOption != currentSecurityOption.value) {
                        // 지문 -> PIN은 이미 PIN이 설정되어 있기에 사용옵션만 저장
                        if (currentSecurityOption.value == 0 && checkedSecurityOption == 1) { // 사용 안 함 -> PIN
                            val intent = Intent(requireContext(), SecurityActivity::class.java)
                            intent.putExtra("revise", true)
                            optionChangeLauncher.launch(intent)
                        } else if (checkedSecurityOption == 2) { // 사용 안 함 or PIN -> 지문
                            // TODO: 지문 인증
                        } else if (checkedSecurityOption == 0) {
                            viewModel.saveSecurityOption(checkedSecurityOption)
                        }
                    }
                    dialog.dismiss()
                }.setSingleChoiceItems(securityOptionItems, currentSecurityOption.value) { dialog, which ->
                    checkedSecurityOption = which
                }.show()
        }

        binding.tvChangePin.setOnClickListener {
            val intent = Intent(requireContext(), SecurityActivity::class.java)
            intent.putExtra("revise", true)
            pinChangeLauncher.launch(intent)
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
}
