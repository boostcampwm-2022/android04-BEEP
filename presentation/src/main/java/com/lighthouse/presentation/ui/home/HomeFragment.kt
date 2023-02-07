package com.lighthouse.presentation.ui.home

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.lighthouse.core.android.exts.dp
import com.lighthouse.core.android.exts.screenWidth
import com.lighthouse.core.android.utils.permission.LocationPermissionManager
import com.lighthouse.core.android.utils.permission.core.permissions
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.FragmentHomeBinding
import com.lighthouse.presentation.extension.repeatOnStarted
import com.lighthouse.presentation.extra.Extras
import com.lighthouse.presentation.model.BrandPlaceInfoUiModel
import com.lighthouse.presentation.ui.common.GifticonViewHolderType
import com.lighthouse.presentation.ui.common.UiState
import com.lighthouse.presentation.ui.common.dialog.ConfirmationDialog
import com.lighthouse.presentation.ui.common.viewBindings
import com.lighthouse.presentation.ui.detailgifticon.GifticonDetailActivity
import com.lighthouse.presentation.ui.home.adapter.NearGifticonAdapter
import com.lighthouse.presentation.ui.main.MainViewModel
import com.lighthouse.presentation.ui.map.MapActivity
import com.lighthouse.presentation.ui.map.adapter.GifticonAdapter
import com.lighthouse.presentation.utils.recycler.ListSpaceItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val binding: FragmentHomeBinding by viewBindings()
    private val homeViewModel: HomeViewModel by viewModels({ requireParentFragment() })
    private val mainViewModel: MainViewModel by activityViewModels()
    private val locationPermission: LocationPermissionManager by permissions()

    private val locationPermissionDialog by lazy {
        val title = getString(R.string.confirmation_title)
        val message = getString(R.string.confirmation_location_message)
        ConfirmationDialog().apply {
            setTitle(title)
            setMessage(message)
            setOnOkClickListener {
                val intent = Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", requireActivity().packageName, null)
                }
                startActivity(intent)
            }
        }
    }

    private val contract = ActivityResultContracts.RequestMultiplePermissions()

    private val locationPermissionLauncher =
        registerForActivityResult(contract) { results ->
            if (results.all { it.value }.not()) {
                locationPermissionDialog
                    .show(parentFragmentManager, ConfirmationDialog::class.java.name)
            }
        }

    private val nearGifticonAdapter = NearGifticonAdapter { gifticon ->
        gotoGifticonDetail(gifticon.id)
    }
    private val expireGifticonAdapter =
        GifticonAdapter(GifticonViewHolderType.VERTICAL) { gifticon ->
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
        end = (screenWidth * 0.05).toFloat(),
        bottom = 4.dp
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.mainVm = mainViewModel
        binding.homeVm = homeViewModel
        observeLocationPermission()
        setBindingAdapter()
        setObserveViewModel()
    }

    private fun observeLocationPermission() {
        viewLifecycleOwner.repeatOnStarted {
            locationPermission.permissionFlow.collectLatest {
                homeViewModel.updateLocationPermission(it)
            }
        }
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
        viewLifecycleOwner.repeatOnStarted {
            homeViewModel.uiState.collectLatest { state ->
                when (state) {
                    is UiState.NetworkFailure -> showSnackBar(R.string.error_network_error)
                    is UiState.Failure -> showSnackBar(R.string.error_network_failure)
                    else -> Unit
                }
            }
        }

        viewLifecycleOwner.repeatOnStarted {
            homeViewModel.eventFlow.collectLatest { directions ->
                when (directions) {
                    is HomeEvent.NavigateMap -> gotoMap(directions.nearBrandsInfo)
                    is HomeEvent.RequestLocationPermissionCheck -> launchPermission()
                }
            }
        }
    }

    private fun gotoMap(
        nearBrandsInfo: List<BrandPlaceInfoUiModel> = emptyList()
    ) {
        when (locationPermission.isGrant) {
            true -> startMapActivity(nearBrandsInfo)
            false -> launchPermission()
        }
    }

    private fun launchPermission() {
        when {
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                locationPermissionDialog
                    .show(parentFragmentManager, ConfirmationDialog::class.java.name)
            }

            else -> locationPermissionLauncher.launch(PERMISSIONS)
        }
    }

    private fun startMapActivity(nearBrandsInfo: List<BrandPlaceInfoUiModel>) {
        startActivity(
            Intent(requireContext(), MapActivity::class.java).apply {
                putExtra(Extras.KEY_NEAR_BRANDS, ArrayList(nearBrandsInfo))
            }
        )
    }

    private fun showSnackBar(@StringRes message: Int) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onStop() {
        homeViewModel.cancelLocationCollectJob()
        super.onStop()
    }

    companion object {
        val PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }
}
