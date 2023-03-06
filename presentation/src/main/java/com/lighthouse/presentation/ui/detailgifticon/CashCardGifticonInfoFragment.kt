package com.lighthouse.presentation.ui.detailgifticon

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.FragmentCashCardGifticonInfoBinding
import com.lighthouse.presentation.ui.common.viewBindings
import com.lighthouse.presentation.util.Geography
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CashCardGifticonInfoFragment : Fragment(R.layout.fragment_cash_card_gifticon_info) {
    val binding: FragmentCashCardGifticonInfoBinding by viewBindings()
    private val viewModel: GifticonDetailViewModel by activityViewModels()

    @Inject
    lateinit var geography: Geography

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel
        binding.geo = geography
        binding.lifecycleOwner = viewLifecycleOwner
        binding.ctfBalance.addOnValueListener {
            viewModel.editBalance(it)
        }
    }
}
