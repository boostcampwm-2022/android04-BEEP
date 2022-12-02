package com.lighthouse.presentation.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.FragmentHomeBinding
import com.lighthouse.presentation.extension.dp
import com.lighthouse.presentation.extension.repeatOnStarted
import com.lighthouse.presentation.extension.screenWidth
import com.lighthouse.presentation.extra.Extras
import com.lighthouse.presentation.model.BrandPlaceInfoUiModel
import com.lighthouse.presentation.model.GifticonUiModel
import com.lighthouse.presentation.ui.common.GifticonViewHolderType
import com.lighthouse.presentation.ui.common.UiState
import com.lighthouse.presentation.ui.common.dialog.ConfirmationDialog
import com.lighthouse.presentation.ui.common.viewBindings
import com.lighthouse.presentation.ui.detailgifticon.GifticonDetailActivity
import com.lighthouse.presentation.ui.home.adapter.NearGifticonAdapter
import com.lighthouse.presentation.ui.main.MainViewModel
import com.lighthouse.presentation.ui.map.MapActivity
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
    private var getResultImage: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (isPermissionGranted()) {
                Timber.tag("TAG").d("${javaClass.simpleName} 외부 설정에서 허용을 하고 왔다.")
                homeViewModel.changeLocationPermission(true)
            } else {
                Timber.tag("TAG").d("${javaClass.simpleName} 외부 설정에서 허용을 하고 오지 않았다.")
            }
        }

    private val locationPermissionDialog by lazy {
        val title = getString(R.string.confirmation_title)
        val message = getString(R.string.confirmation_location_message)
        ConfirmationDialog().apply {
            setTitle(title)
            setMessage(message)
            setOnOkClickListener {
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.data = Uri.fromParts("package", requireActivity().packageName, null)
                getResultImage.launch(intent)
            }
        }
    }

    private val contract = ActivityResultContracts.RequestMultiplePermissions()

    private val locationPermissionLauncher =
        registerForActivityResult(contract) { results ->
            if (results.all { it.value }) {
                Timber.tag("TAG").d("${javaClass.simpleName} 권한을 허용해줬다.")
                homeViewModel.changeLocationPermission(true)
                gotoMap()
            } else {
                Timber.tag("TAG").d("${javaClass.simpleName} 하나라도 안됐다")
            }
        }

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
        start = (screenWidth * 0.05).toFloat(),
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
                Timber.tag("TAG").d("${javaClass.simpleName} UiState -> $state")
                when (state) {
                    is UiState.Success -> updateNearGifticon(state.item)
                    is UiState.Loading -> startShimmer()
                    is UiState.NetworkFailure -> showSnackBar(R.string.error_network_error)
                    is UiState.NotFoundResults -> guideNotFoundNearBrands()
                    is UiState.Failure -> showSnackBar(R.string.error_network_failure)
                }
            }
        }

        repeatOnStarted {
            homeViewModel.eventFlow.collectLatest { directions ->
                when (directions) {
                    is HomeEvent.NavigateMap -> gotoMap(directions.gifticons, directions.nearBrandsInfo)
                    is HomeEvent.RequestLocationPermissionCheck -> launchPermission()
                }
            }
        }

        repeatOnStarted {
            homeViewModel.hasLocationPermission.collectLatest { hasPermission ->
                if (hasPermission.not()) stopShimmer()
                binding.tvNotAllowLocationPermission.isVisible = hasPermission.not()
                binding.btnLocationPermissionCheck.isVisible = hasPermission.not()
            }
        }
    }

    private fun gotoMap(
        gifticons: List<Gifticon> = emptyList(),
        nearBrandsInfo: List<BrandPlaceInfoUiModel> = emptyList()
    ) {
        when (isPermissionGranted()) {
            true -> startMapActivity(nearBrandsInfo, gifticons)
            else -> launchPermission()
        }
    }

    private fun launchPermission() {
        when {
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                locationPermissionDialog.show(parentFragmentManager, ConfirmationDialog::class.java.name)
            }
            else -> locationPermissionLauncher.launch(PERMISSIONS)
        }
    }

    private fun startMapActivity(nearBrandsInfo: List<BrandPlaceInfoUiModel>, gifticons: List<Gifticon>) {
        startActivity(
            Intent(requireContext(), MapActivity::class.java).apply {
                putExtra(Extras.KEY_NEAR_BRANDS, ArrayList(nearBrandsInfo))
                putExtra(Extras.KEY_NEAR_GIFTICONS, ArrayList(gifticons))
            }
        )
    }

    private fun isPermissionGranted(): Boolean {
        for (permission in PERMISSIONS) {
            val result: Int = ContextCompat.checkSelfPermission(requireContext(), permission)
            if (PackageManager.PERMISSION_GRANTED != result) {
                return false
            }
        }
        return true
    }

    private fun startShimmer() {
        binding.tvEmptyNearBrands.isVisible = false
        binding.shimmer.isVisible = true
        binding.shimmer.startShimmer()
    }

    private fun updateNearGifticon(item: List<GifticonUiModel>) {
        binding.tvEmptyNearBrands.isVisible = false
        nearGifticonAdapter.submitList(item)
        stopShimmer()
    }

    private fun guideNotFoundNearBrands() {
        binding.tvEmptyNearBrands.isVisible = true
        nearGifticonAdapter.submitList(emptyList())
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

    companion object {
        private val PERMISSIONS =
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    }
}
