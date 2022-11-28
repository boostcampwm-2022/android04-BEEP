package com.lighthouse.presentation.ui.home

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.FragmentHomeBinding
import com.lighthouse.presentation.extension.dp
import com.lighthouse.presentation.extension.repeatOnStarted
import com.lighthouse.presentation.ui.common.GifticonViewHolderType
import com.lighthouse.presentation.ui.common.UiState
import com.lighthouse.presentation.ui.common.viewBindings
import com.lighthouse.presentation.ui.main.MainViewModel
import com.lighthouse.presentation.ui.map.adapter.GifticonAdapter
import com.lighthouse.presentation.util.recycler.ListSpaceItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

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
        setObserveViewModel()
        binding.vm = mainViewModel
    }

    private fun setBindingAdapter() {
        with(binding.rvNearGifticon) {
            adapter = nearGifticonAdapter
            addItemDecoration(itemDecoration)
            val nearGifticon = homeViewModel.nearGifticon.value
            if (nearGifticon is UiState.Success) {
                nearGifticonAdapter.submitList(nearGifticon.item)
            }
        }
        with(binding.rvExpireGifticon) {
            adapter = expireGifticonAdapter
            addItemDecoration(itemDecoration)
            /*val allGifticon = homeViewModel.allGifticons.value
            if (allGifticon.isNotEmpty()) {
                expireGifticonAdapter.submitList(allGifticon)
            }*/
        }
    }

    private fun setObserveViewModel() {
        repeatOnStarted {
            homeViewModel.allGifticons.collectLatest {
                Timber.tag("TAG").d("${javaClass.simpleName} $it")
                expireGifticonAdapter.submitList(it)
            }
        }

        repeatOnStarted {
            homeViewModel.nearGifticon.collectLatest { state ->
                when (state) {
                    is UiState.Success -> updateNearGifticon(state.item)
                    is UiState.Loading -> Unit // TODO 로딩화면 처리 필요
                    is UiState.NetworkFailure -> showSnackBar(R.string.error_network_error)
                    is UiState.NotFoundResults -> showSnackBar(R.string.error_not_found_results)
                    is UiState.Failure -> showSnackBar(R.string.error_network_failure)
                }
            }
        }
    }

    private fun updateNearGifticon(item: List<Gifticon>) {
        nearGifticonAdapter.submitList(item)
    }

    private fun showSnackBar(@StringRes message: Int) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.tag("TAG").d("${javaClass.simpleName} onDestroyView")
    }
}
