package com.lighthouse.presentation.ui.map

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.lighthouse.domain.LocationConverter
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.ActivityMapBinding
import com.lighthouse.presentation.extension.dp
import com.lighthouse.presentation.extension.repeatOnStarted
import com.lighthouse.presentation.extension.screenHeight
import com.lighthouse.presentation.extra.Extras
import com.lighthouse.presentation.extra.Extras.CATEGORY_ACCOMMODATION
import com.lighthouse.presentation.extra.Extras.CATEGORY_CAFE
import com.lighthouse.presentation.extra.Extras.CATEGORY_CONVENIENCE
import com.lighthouse.presentation.extra.Extras.CATEGORY_CULTURE
import com.lighthouse.presentation.extra.Extras.CATEGORY_MART
import com.lighthouse.presentation.extra.Extras.CATEGORY_RESTAURANT
import com.lighthouse.presentation.model.BrandPlaceInfoUiModel
import com.lighthouse.presentation.ui.common.GifticonViewHolderType
import com.lighthouse.presentation.ui.common.UiState
import com.lighthouse.presentation.ui.detailgifticon.GifticonDetailActivity
import com.lighthouse.presentation.ui.map.adapter.GifticonAdapter
import com.lighthouse.presentation.util.recycler.ListSpaceItemDecoration
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.widget.LocationButtonView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("MissingPermission")
@AndroidEntryPoint
class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapBinding
    private lateinit var naverMap: NaverMap
    private lateinit var mapView: MapView
    private lateinit var client: FusedLocationProviderClient
    private lateinit var fusedLocationSource: FusedLocationSource
    private val viewModel: MapViewModel by viewModels()
    private val gifticonAdapter = GifticonAdapter(GifticonViewHolderType.HORIZONTAL) { gifticon ->
        startActivity(
            Intent(this, GifticonDetailActivity::class.java).apply {
                putExtra(Extras.KEY_GIFTICON_ID, gifticon.id)
            }
        )
    }
    private val currentLocationButton: LocationButtonView by lazy { binding.btnCurrentLocation }
    private val infoWindow: InfoWindow by lazy { InfoWindow() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        client = LocationServices.getFusedLocationProviderClient(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_map)
        binding.vm = viewModel
        binding.lifecycleOwner = this
        mapView = binding.mapView.apply {
            onCreate(savedInstanceState)
            getMapAsync(this@MapActivity)
        }
        setBottomGifticonSheet()
        setObserveEvent()
        setInfoWindow()
    }

    private fun setBottomGifticonSheet() {
        binding.layoutDialog.cardContainer.apply {
            layoutParams = layoutParams.apply {
                height = (screenHeight * 0.48).toInt()
            }
        }
        with(binding.layoutDialog.rvGifticons) {
            adapter = gifticonAdapter
            addItemDecoration(
                ListSpaceItemDecoration(
                    space = 12.dp,
                    top = 12.dp,
                    bottom = 24.dp,
                    start = 12.dp,
                    end = 12.dp
                )
            )
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onMapReady(map: NaverMap) {
        fusedLocationSource = FusedLocationSource(this, PERMISSION_REQUEST_CODE)
        naverMap = map.apply {
            this.locationSource = fusedLocationSource
            setOnMapClickListener { _, _ ->
                resetFocusMarker(viewModel.focusMarker)
                viewModel.updateGifticons()
            }
        }
        currentLocationButton.map = naverMap

        setInitSearchData()
        setObserveSearchData()
        setNaverMapZoom()
        setObserveEvent()
    }

    // configuration change 일어날때 viewModel이 갖고 있는 marker 데이터 있는지 확인
    private fun setInitSearchData() {
        viewModel.markerHolder.forEach { marker ->
            marker.map = naverMap
        }
    }

    private fun setFocusMarker(marker: Marker) {
        marker.iconTintColor = getColor(R.color.beep_pink)
        marker.captionColor = getColor(R.color.beep_pink)
        marker.zIndex = 1
        val location = marker.position
        viewModel.updateFocusMarker(marker)
        moveMapCamera(location.longitude, location.latitude)
    }

    private fun setGifticonAdapterChangeCallback() {
        binding.vpGifticon.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (viewModel.viewPagerFocus.not()) {
                    viewModel.updatePagerFocus(true)
                    return
                }
                if (isRecentSelected(gifticonAdapter.currentList[position].brandLowerName)) return
                val currentItem = gifticonAdapter.currentList[position].brandLowerName
                findBrandPlaceInfo(currentItem)
            }
        })
    }

    /**
     * @param brandName 찾고자하는 브랜드명
     */
    private fun findBrandPlaceInfo(brandName: String, isLoadGifticonList: Boolean = false) {
        client.lastLocation.addOnSuccessListener { currentLocation ->
            val brandPlaceInfo = viewModel.brandInfos.filter { brandPlaceInfo ->
                brandPlaceInfo.brandLowerName == brandName
            }.minByOrNull { location ->
                diffLocation(
                    location.x,
                    location.y,
                    currentLocation.longitude,
                    currentLocation.latitude
                )
            } ?: return@addOnSuccessListener

            resetFocusMarker(viewModel.focusMarker)

            val currentFocusMarker = viewModel.markerHolder.find {
                currentLocation(it, brandPlaceInfo)
            } ?: return@addOnSuccessListener
            moveMapCamera(brandPlaceInfo.x.toDouble(), brandPlaceInfo.y.toDouble())
            setFocusMarker(currentFocusMarker)
            if (isLoadGifticonList) viewModel.updateGifticons()
        }
    }

    private fun currentLocation(it: Marker, brandPlaceInfo: BrandPlaceInfoUiModel) =
        it.position.longitude == brandPlaceInfo.x.toDouble() && it.position.latitude == brandPlaceInfo.y.toDouble()

    private fun isRecentSelected(brand: String) =
        viewModel.recentSelectedMarker.captionText.lowercase() == brand
        
    private fun moveMapCamera(longitude: Double, latitude: Double) {
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(latitude, longitude))
            .animate(CameraAnimation.Easing)
        naverMap.moveCamera(cameraUpdate)
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

    private fun setNaverMapZoom() {
        naverMap.maxZoom = 18.0
        naverMap.minZoom = 7.0

        client.lastLocation.addOnSuccessListener { startLocation ->
            moveMapCamera(startLocation.longitude, startLocation.latitude)
        }
    }

    private fun updateBrandMarker(brandPlaceSearchResults: List<BrandPlaceInfoUiModel>) {
        val brandMarkers = brandPlaceSearchResults.map { brandPlaceSearchResult ->
            Marker().apply {
                val latLng =
                    LatLng(brandPlaceSearchResult.y.toDouble(), brandPlaceSearchResult.x.toDouble())
                setMarker(this, latLng, brandPlaceSearchResult)
            }
        }
        viewModel.updateMarkers(brandMarkers)

        repeatOnStarted {
            viewModel.widgetBrand.collect { brand ->
                findBrandPlaceInfo(brand)
            }
        }
    }

    private fun findBrandPlaceInfo(brandName: String) {
        client.lastLocation.addOnSuccessListener { currentLocation ->
            val brandPlaceInfo = viewModel.brandInfos.filter { brandPlaceInfo ->
                brandPlaceInfo.brandLowerName == brandName
            }.minByOrNull { location ->
                LocationConverter.diffLocation(
                    location.x,
                    location.y,
                    currentLocation.longitude,
                    currentLocation.latitude
                )
            } ?: return@addOnSuccessListener

            resetFocusMarker(viewModel.focusMarker)

            val currentFocusMarker = viewModel.markerHolder.find {
                currentLocation(it, brandPlaceInfo)
            } ?: return@addOnSuccessListener
            moveMapCamera(brandPlaceInfo.x.toDouble(), brandPlaceInfo.y.toDouble())
            setFocusMarker(currentFocusMarker)
            viewModel.updateGifticons()
        }
    }

    private fun currentLocation(it: Marker, brandPlaceInfo: BrandPlaceInfoUiModel) =
        it.position.longitude == brandPlaceInfo.x.toDouble() &&
            it.position.latitude == brandPlaceInfo.y.toDouble()

    private fun setMarker(
        marker: Marker,
        latLng: LatLng,
        brandPlaceSearchResult: BrandPlaceInfoUiModel
    ) {
        with(marker) {
            position = latLng
            icon = setMarkerIcon(brandPlaceSearchResult.categoryName)
            iconTintColor = getColor(R.color.point_green)
            tag = brandPlaceSearchResult.placeUrl
            map = naverMap
            captionText = brandPlaceSearchResult.brand.uppercase()
            isHideCollidedSymbols = true
            zIndex = 0
        }
        marker.setOnClickListener {
            if (isSameMarker(marker)) return@setOnClickListener true
            val curFocusBrand = viewModel.focusMarker
            resetFocusMarker(curFocusBrand)
            setFocusMarker(marker)
            viewModel.updateFocusMarker(marker)
            viewModel.updateGifticons()
            infoWindow.open(marker)
            true
        }
    }

    private fun setMarkerIcon(categoryName: String): OverlayImage {
        return when (categoryName) {
            CATEGORY_MART -> OverlayImage.fromResource(R.drawable.ic_marker_market)
            CATEGORY_CONVENIENCE -> OverlayImage.fromResource(R.drawable.ic_marker_convenience)
            CATEGORY_CULTURE -> OverlayImage.fromResource(R.drawable.ic_marker_culture)
            CATEGORY_ACCOMMODATION -> OverlayImage.fromResource(R.drawable.ic_marker_accommodation)
            CATEGORY_RESTAURANT -> OverlayImage.fromResource(R.drawable.ic_marker_restaurant)
            CATEGORY_CAFE -> OverlayImage.fromResource(R.drawable.ic_marker_cafe)
            else -> OverlayImage.fromResource(R.drawable.ic_marker_base)
        }
    }

    private fun isSameMarker(marker: Marker) =
        marker.position.longitude == viewModel.focusMarker.position.longitude &&
            marker.position.latitude == viewModel.focusMarker.position.latitude

    private fun resetFocusMarker(marker: Marker) {
        marker.zIndex = 0
        marker.iconTintColor = getColor(R.color.point_green)
        marker.captionColor = getColor(R.color.black)
        viewModel.resetMarker()
        infoWindow.close()
    }

    private fun setInfoWindow() {
        infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(this) {
            override fun getText(infoWindow: InfoWindow): CharSequence {
                return getString(R.string.map_goto_homepage)
            }
        }
        infoWindow.setOnClickListener {
            infoWindow.marker?.let { marker ->
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(marker.tag.toString()))
                startActivity(browserIntent)
            }
            true
        }
    }

    private fun setObserveEvent() {
        repeatOnStarted {
            viewModel.event.collect { event ->
                when (event) {
                    is MapEvent.DeleteMarker -> deleteMarker(event.marker)
                    is MapEvent.NavigateHome -> gotoHome()
                }
            }
        }
    }

    private fun deleteMarker(marker: List<Marker>) {
        marker.forEach { it.map = null }
    }

    private fun gotoHome() {
        finish()
    }

    private fun showSnackBar(@StringRes message: Int) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
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
        super.onDestroy()
    }

    override fun onLowMemory() {
        mapView.onLowMemory()
        super.onLowMemory()
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }
}
