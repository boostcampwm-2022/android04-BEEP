package com.lighthouse.presentation.ui.detailgifticon

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.FragmentCashCardGifticonInfoBinding
import com.lighthouse.presentation.ui.common.ValueListener
import com.lighthouse.presentation.ui.common.viewBindings
import timber.log.Timber

class CashCardGifticonInfoFragment : Fragment(R.layout.fragment_cash_card_gifticon_info), ValueListener {
    val binding: FragmentCashCardGifticonInfoBinding by viewBindings()
    private val viewModel: GifticonDetailViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.ctfBalance.addOnValueListener(this)
    }

    override fun onValueChanged(value: Int) {
        Timber.tag("Custom").d("value: $value")
        viewModel.editBalance(value)
    }
}
