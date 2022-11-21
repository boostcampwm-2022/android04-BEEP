package com.lighthouse.presentation.ui.security.pin

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.FragmentPinBinding
import com.lighthouse.presentation.ui.common.viewBindings

class PinFragment : Fragment(R.layout.fragment_pin) {

    private val binding by viewBindings(FragmentPinBinding::bind)
    private val viewModel: PinViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = this
    }
}
