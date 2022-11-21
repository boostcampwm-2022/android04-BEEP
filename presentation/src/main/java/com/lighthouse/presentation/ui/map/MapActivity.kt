package com.lighthouse.presentation.ui.map

import android.location.Location
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.ActivityMapBinding
import com.lighthouse.presentation.extension.repeatOnStarted
import com.lighthouse.presentation.model.BrandPlaceInfoUiModel
import com.lighthouse.presentation.ui.common.UiState
import com.lighthouse.presentation.ui.map.adapter.MapGifticonAdapter
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.FusedLocationSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.util.Date
import java.util.UUID

@AndroidEntryPoint
class MapActivity : AppCompatActivity(), OnMapReadyCallback, Overlay.OnClickListener, OnLocationUpdateListener {

    private lateinit var binding: ActivityMapBinding
    private lateinit var naverMap: NaverMap
    private lateinit var mapView: MapView
    private lateinit var fusedLocationProviderClient: FusedLocationProvider
    private lateinit var locationSource: FusedLocationSource
    private val viewModel: MapViewModel by viewModels()
    private val adapter = MapGifticonAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_map)
        mapView = binding.mapView.apply {
            onCreate(savedInstanceState)
            getMapAsync(this@MapActivity)
        }

        setGifticonAdapter()
        setObserveSearchData()
        setFusedLocationProvider()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    private fun setObserveSearchData() {
        repeatOnStarted {
            viewModel.state.collectLatest { state ->
                when (state) {
                    is UiState.Success -> updateBrandMarker(state.item)
                    is UiState.Loading -> Unit
                    is UiState.NetworkFailure -> showSnackBar(R.string.error_network_error)
                    is UiState.NotFoundResults -> showSnackBar(R.string.error_not_found_results)
                    is UiState.Failure -> showSnackBar(R.string.error_network_failure)
                }
            }
        }
    }

    private fun showSnackBar(@StringRes message: Int) {
        Snackbar.make(binding.layoutMap, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun updateBrandMarker(brandPlaceSearchResults: List<BrandPlaceInfoUiModel>) {
        brandPlaceSearchResults.forEach { brandPlaceSearchResult ->
            val marker = Marker()

            val latLng = LatLng(brandPlaceSearchResult.y.toDouble(), brandPlaceSearchResult.x.toDouble())
            with(marker) {
                position = latLng
                onClickListener = this@MapActivity
                map = naverMap
                width = Marker.SIZE_AUTO
                height = Marker.SIZE_AUTO
                tag = brandPlaceSearchResult.brand
                captionText = brandPlaceSearchResult.brand
            }
        }
    }

    private fun setFusedLocationProvider() {
        fusedLocationProviderClient = FusedLocationProvider(this, this).apply {
            requestLastLocation()
        }
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map
        locationSource = FusedLocationSource(this, 1000)
        naverMap.locationSource = locationSource
        setNaverMapZoom()
    }

    private fun setNaverMapZoom() {
        naverMap.maxZoom = 18.0
        naverMap.minZoom = 10.0
    }

    private fun setGifticonAdapter() {
        val gifticonTestData = listOf(
            Gifticon(UUID.randomUUID().toString(), "이름", "bbq", "bbq", Date(120, 20, 20), "bar", true, 1, "memo", true),
            Gifticon(UUID.randomUUID().toString(), "이름", "bbq", "bbq", Date(122, 11, 15), "bar", true, 1, "memo", true),
            Gifticon(UUID.randomUUID().toString(), "이름", "bbq", "bbq", Date(122, 5, 10), "bar", true, 1, "memo", true),
            Gifticon(UUID.randomUUID().toString(), "이름", "bbq", "bbq", Date(150, 10, 20), "bar", true, 1, "memo", true),
            Gifticon(UUID.randomUUID().toString(), "이름", "bbq", "bbq", Date(160, 10, 20), "bar", true, 1, "memo", true)
        )
        adapter.submitList(gifticonTestData)
        binding.vpGifticon.adapter = adapter
    }

    override fun onClick(overlay: Overlay): Boolean {
        return true
    }

    override fun onLocationUpdated(location: Location) {
        viewModel.getBrandPlaceInfos(location.longitude, location.latitude)
        moveMapCamera(location.longitude, location.latitude)
    }

    private fun moveMapCamera(longitude: Double, latitude: Double) {
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(latitude, longitude))
        naverMap.moveCamera(cameraUpdate)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mapView.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onStop() {
        mapView.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        fusedLocationProviderClient.stopLocationUpdates()
        super.onDestroy()
    }

    override fun onLowMemory() {
        mapView.onLowMemory()
        super.onLowMemory()
    }
}
