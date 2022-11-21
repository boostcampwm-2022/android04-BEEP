package com.lighthouse.presentation.ui.security.pin

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.FragmentPinBinding
import com.lighthouse.presentation.ui.common.viewBindings
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
                    PinSettingType.INITIAL -> binding.tvPinDescription.text = getString(R.string.pin_initial_description)
                    PinSettingType.CONFIRM -> binding.tvPinDescription.text = getString(R.string.pin_confirm_description)
                    PinSettingType.WRONG -> binding.tvPinDescription.text = getString(R.string.pin_wrong_description)
                    PinSettingType.COMPLETE -> (requireActivity() as SecurityActivity).moveScreen(SecurityDirections.FINGERPRINT)
                }
            }
        }
    }
}