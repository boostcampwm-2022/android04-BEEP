package com.lighthouse.presentation.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.FragmentHomeBinding
import com.lighthouse.presentation.extension.dp
import com.lighthouse.presentation.ui.common.GifticonViewHolderType
import com.lighthouse.presentation.ui.common.viewBindings
import com.lighthouse.presentation.ui.main.MainViewModel
import com.lighthouse.presentation.ui.map.adapter.GifticonAdapter
import com.lighthouse.presentation.util.recycler.ListSpaceItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val binding by viewBindings(FragmentHomeBinding::bind)
    private val mainViewModel: MainViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private val nearGifticonAdapter = GifticonAdapter(GifticonViewHolderType.VERTICAL)
    private val expireGifticonAdapter = GifticonAdapter(GifticonViewHolderType.VERTICAL)
    private val itemDecoration = ListSpaceItemDecoration(
        space = 8.dp,
        start = 4.dp,
        top = 4.dp,
        end = 24.dp,
        bottom = 4.dp
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
        homeViewModel
    }
}
