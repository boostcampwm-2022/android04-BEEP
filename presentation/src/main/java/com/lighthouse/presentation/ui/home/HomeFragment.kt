package com.lighthouse.presentation.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.FragmentHomeBinding
import com.lighthouse.presentation.extension.dp
import com.lighthouse.presentation.extension.repeatOnStarted
import com.lighthouse.presentation.extra.Extras
import com.lighthouse.presentation.model.GifticonUiModel
import com.lighthouse.presentation.ui.common.GifticonViewHolderType
import com.lighthouse.presentation.ui.common.UiState
import com.lighthouse.presentation.ui.common.viewBindings
import com.lighthouse.presentation.ui.detailgifticon.GifticonDetailActivity
import com.lighthouse.presentation.ui.home.adapter.NearGifticonAdapter
import com.lighthouse.presentation.ui.main.MainViewModel
import com.lighthouse.presentation.ui.map.adapter.GifticonAdapter
import com.lighthouse.presentation.util.recycler.ListSpaceItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val binding: FragmentHomeBinding by viewBindings()
    private val homeViewModel: HomeViewModel by viewModels({ requireParentFragment() })
    private val mainViewModel: MainViewModel by activityViewModels()

    private val nearGifticonAdapter = NearGifticonAdapter { gifticon ->
        gotoGifticonDetail(gifticon.id)
    }
    private val expireGifticonAdapter = GifticonAdapter(GifticonViewHolderType.VERTICAL) { gifticon ->
        gotoGifticonDetail(gifticon.id)
    }

    private fun gotoGifticonDetail(id: String) {
        startActivity(
            Intent(requireContext(), GifticonDetailActivity::class.java).apply {
                putExtra(Extras.KEY_GIFTICON_ID, id)
            }
        )
    }

    private val itemDecoration = ListSpaceItemDecoration(
        space = 8.dp,
        top = 4.dp,
        end = 24.dp,
        bottom = 4.dp
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.mainVm = mainViewModel
        binding.homeVm = homeViewModel
        setBindingAdapter()
        setObserveViewModel()
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
    }

    private fun setObserveViewModel() {
        repeatOnStarted {
            homeViewModel.nearGifticon.collectLatest { state ->
                when (state) {
                    is UiState.Success -> updateNearGifticon(state.item)
                    is UiState.Loading -> startShimmer()
                    is UiState.NetworkFailure -> showSnackBar(R.string.error_network_error)
                    is UiState.NotFoundResults -> showSnackBar(R.string.error_not_found_results)
                    is UiState.Failure -> showSnackBar(R.string.error_network_failure)
                    is UiState.NotLocationPermission -> guideLocationPermission()
                }
            }
        }
    }

    private fun guideLocationPermission() {
        stopShimmer()
        // TODO 확인용 입니다. 나중에 지워질 예정이에요.
        Timber.tag("TAG").d("${javaClass.simpleName} 위치 권한이 허용 돼 있지 않다~")
    }

    private fun startShimmer() {
        binding.shimmer.isVisible = true
        binding.shimmer.startShimmer()
    }

    private fun updateNearGifticon(item: List<GifticonUiModel>) {
        nearGifticonAdapter.submitList(item)
        stopShimmer()
    }

    private fun showSnackBar(@StringRes message: Int) {
        stopShimmer()
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun stopShimmer() {
        binding.shimmer.stopShimmer()
        binding.shimmer.isVisible = false
    }
}
