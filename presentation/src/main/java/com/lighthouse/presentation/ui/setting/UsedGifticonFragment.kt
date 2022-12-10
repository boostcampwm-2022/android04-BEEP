package com.lighthouse.presentation.ui.setting

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.FragmentUsedGifticonBinding
import com.lighthouse.presentation.extension.dp
import com.lighthouse.presentation.extra.Extras
import com.lighthouse.presentation.ui.common.GifticonViewHolderType
import com.lighthouse.presentation.ui.common.viewBindings
import com.lighthouse.presentation.ui.detailgifticon.GifticonDetailActivity
import com.lighthouse.presentation.ui.map.adapter.GifticonAdapter
import com.lighthouse.presentation.util.recycler.ListSpaceItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UsedGifticonFragment : Fragment(R.layout.fragment_used_gifticon) {

    private val binding: FragmentUsedGifticonBinding by viewBindings()
    private val viewModel: UsedGifticonViewModel by viewModels()

    private val itemDecoration = ListSpaceItemDecoration(
        space = 4.dp,
        start = 2.dp,
        top = 4.dp,
        end = 2.dp,
        bottom = 4.dp
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = viewModel

        with(binding.rvUsedGifticon) {
            adapter = GifticonAdapter(GifticonViewHolderType.VERTICAL) { gifticon ->
                gotoGifticonDetail(gifticon.id)
                addItemDecoration(itemDecoration)
            }
        }
    }

    private fun gotoGifticonDetail(id: String) {
        startActivity(
            Intent(requireContext(), GifticonDetailActivity::class.java).apply {
                putExtra(Extras.KEY_GIFTICON_ID, id)
            }
        )
    }
}
