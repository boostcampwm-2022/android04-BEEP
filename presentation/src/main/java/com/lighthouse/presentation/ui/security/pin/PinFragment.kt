package com.lighthouse.presentation.ui.security.pin

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.FragmentPinBinding
import com.lighthouse.presentation.ui.common.viewBindings
import com.lighthouse.presentation.ui.main.MainActivity
import com.lighthouse.presentation.ui.security.SecurityActivity
import com.lighthouse.presentation.ui.security.event.SecurityDirections
import kotlinx.coroutines.launch

class PinFragment : Fragment(R.layout.fragment_pin) {

    private val binding by viewBindings(FragmentPinBinding::bind)
    private val viewModel: PinViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = this

        lifecycleScope.launch {
            viewModel.pinMode.collect {
                when (it) {
                    PinSettingType.INITIAL -> {
                        binding.tvPinDescription.text = getString(R.string.pin_initial_description)
                        binding.btnPinPrev.visibility = View.GONE
                    }
                    PinSettingType.CONFIRM -> {
                        binding.tvPinDescription.text = getString(R.string.pin_confirm_description)
                        binding.btnPinPrev.visibility = View.VISIBLE
                    }
                    PinSettingType.WRONG -> binding.tvPinDescription.text = getString(R.string.pin_wrong_description)
                    PinSettingType.COMPLETE -> (requireActivity() as SecurityActivity).moveScreen(SecurityDirections.FINGERPRINT)
                }
            }
        }

        binding.tvSecureNotUse.setOnClickListener {
            // TODO: 보안 설정 사용 X 저장
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
