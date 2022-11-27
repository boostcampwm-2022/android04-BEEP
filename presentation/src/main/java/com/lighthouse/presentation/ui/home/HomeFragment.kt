package com.lighthouse.presentation.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.FragmentHomeBinding
import com.lighthouse.presentation.extension.dp
import com.lighthouse.presentation.ui.common.GifticonViewHolderType
import com.lighthouse.presentation.ui.common.viewBindings
import com.lighthouse.presentation.ui.main.MainViewModel
import com.lighthouse.presentation.ui.map.adapter.GifticonAdapter
import com.lighthouse.presentation.util.UUID
import com.lighthouse.presentation.util.recycler.ListSpaceItemDecoration
import java.util.Date

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val binding by viewBindings(FragmentHomeBinding::bind)
    private val mainViewModel: MainViewModel by activityViewModels()
    private val nearGifticonAdapter = GifticonAdapter(GifticonViewHolderType.VERTICAL)
    private val expireGifticonAdapter = GifticonAdapter(GifticonViewHolderType.VERTICAL)
    private val itemDecoration = ListSpaceItemDecoration(
        space = 8.dp,
        start = 4.dp,
        top = 4.dp,
        end = 24.dp,
        bottom = 4.dp
    )

    val gifticonTestData = listOf(
        Gifticon(UUID.generate(), "이름", "스타벅스", "스타벅스", Date(120, 20, 20), "bar", true, 1, "memo", true),
        Gifticon(UUID.generate(), "이름", "스타벅스", "스타벅스", Date(120, 20, 20), "bar", true, 1, "memo", true),
        Gifticon(UUID.generate(), "이름", "스타벅스", "스타벅스", Date(120, 20, 20), "bar", true, 1, "memo", true),
        Gifticon(UUID.generate(), "이름", "스타벅스", "스타벅스", Date(120, 20, 20), "bar", true, 1, "memo", true),
        Gifticon(UUID.generate(), "이름", "이름", "베스킨라빈스", Date(122, 11, 15), "bar", true, 1, "memo", true),
        Gifticon(UUID.generate(), "이름", "이름", "베스킨라빈스", Date(122, 11, 15), "bar", true, 1, "memo", true),
        Gifticon(UUID.generate(), "이름", "이름", "베스킨라빈스", Date(122, 11, 15), "bar", true, 1, "memo", true),
        Gifticon(UUID.generate(), "이름", "BHC", "BHC", Date(122, 5, 10), "bar", true, 1, "memo", true),
        Gifticon(UUID.generate(), "이름", "BBQ", "BBQ", Date(150, 10, 20), "bar", true, 1, "memo", true),
        Gifticon(UUID.generate(), "이름", "BBQ", "BBQ", Date(150, 10, 20), "bar", true, 1, "memo", true),
        Gifticon(UUID.generate(), "이름", "BBQ", "BBQ", Date(150, 10, 20), "bar", true, 1, "memo", true),
        Gifticon(UUID.generate(), "이름", "GS25", "GS25", Date(160, 10, 20), "bar", true, 1, "memo", true),
        Gifticon(UUID.generate(), "이름", "GS25", "GS25", Date(160, 10, 20), "bar", true, 1, "memo", true),
        Gifticon(UUID.generate(), "이름", "GS25", "GS25", Date(160, 10, 20), "bar", true, 1, "memo", true),
        Gifticon(UUID.generate(), "이름", "GS25", "GS25", Date(160, 10, 20), "bar", true, 1, "memo", true),
        Gifticon(UUID.generate(), "이름", "CU", "CU", Date(160, 10, 20), "bar", true, 1, "memo", true),
        Gifticon(UUID.generate(), "이름", "CU", "CU", Date(160, 10, 20), "bar", true, 1, "memo", true),
        Gifticon(UUID.generate(), "이름", "CU", "CU", Date(160, 10, 20), "bar", true, 1, "memo", true),
        Gifticon(UUID.generate(), "이름", "CU", "CU", Date(160, 10, 20), "bar", true, 1, "memo", true),
        Gifticon(UUID.generate(), "이름", "서브웨이", "서브웨이", Date(160, 10, 20), "bar", true, 1, "memo", true),
        Gifticon(UUID.generate(), "이름", "서브웨이", "서브웨이", Date(160, 10, 20), "bar", true, 1, "memo", true),
        Gifticon(UUID.generate(), "이름", "세븐일레븐", "세븐일레븐", Date(160, 10, 20), "bar", true, 1, "memo", true),
        Gifticon(UUID.generate(), "이름", "세븐일레븐", "세븐일레븐", Date(160, 10, 20), "bar", true, 1, "memo", true),
        Gifticon(UUID.generate(), "이름", "세븐일레븐", "세븐일레븐", Date(160, 10, 20), "bar", true, 1, "memo", true),
        Gifticon(UUID.generate(), "이름", "세븐일레븐", "세븐일레븐", Date(160, 10, 20), "bar", true, 1, "memo", true),
        Gifticon(UUID.generate(), "이름", "파파존스", "파파존스", Date(160, 10, 20), "bar", true, 1, "memo", true)
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setBindingAdapter()
        binding.vm = mainViewModel
    }

    private fun setBindingAdapter() {
        with(binding.rvNearGifticon) {
            adapter = nearGifticonAdapter
            addItemDecoration(itemDecoration)
        }
        with(binding.rvExpireGifticon) {
            adapter = expireGifticonAdapter
            addItemDecoration(itemDecoration)
        }
        nearGifticonAdapter.submitList(gifticonTestData)
        expireGifticonAdapter.submitList(gifticonTestData)
    }
}
