package com.lighthouse.presentation.ui.detailgifticon

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.FragmentStandardGifticonInfoBinding
import com.lighthouse.presentation.ui.common.viewBindings
import com.lighthouse.presentation.util.Geography

class StandardGifticonInfoFragment : Fragment(R.layout.fragment_standard_gifticon_info) {
    private val binding: FragmentStandardGifticonInfoBinding by viewBindings()
    private val viewModel: GifticonDetailViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.geo = Geography(requireContext())
        binding.lifecycleOwner = viewLifecycleOwner
    }
}
