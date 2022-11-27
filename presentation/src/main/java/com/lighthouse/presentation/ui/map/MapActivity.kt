package com.lighthouse.presentation.ui.map

import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.lighthouse.domain.LocationConverter
import com.lighthouse.domain.LocationConverter.toPolygonLatLng
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.ActivityMapBinding
import com.lighthouse.presentation.extension.dp
import com.lighthouse.presentation.extension.repeatOnStarted
import com.lighthouse.presentation.extension.screenWidth
import com.lighthouse.presentation.model.BrandPlaceInfoUiModel
import com.lighthouse.presentation.ui.common.GifticonViewHolderType
import com.lighthouse.presentation.ui.common.UiState
import com.lighthouse.presentation.ui.map.adapter.GifticonAdapter
import com.lighthouse.presentation.ui.map.event.MarkerClickEvent
import com.lighthouse.presentation.util.recycler.ListSpaceItemDecoration
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Marker.DEFAULT_ICON
import com.naver.maps.map.overlay.PolygonOverlay
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
    private val gifticonAdapter = GifticonAdapter(GifticonViewHolderType.HORIZONTAL)
    private val currentLocationButton: LocationButtonView by lazy { binding.btnCurrentLocation }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        client = LocationServices.getFusedLocationProviderClient(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_map)
        mapView = binding.mapView.apply {
            onCreate(savedInstanceState)
            getMapAsync(this@MapActivity)
        }
        setAdapter()
        setInitAdapterData()
    }

    private fun setAdapter() {
        val pagerWidth = 300.dp
        val pageMarginPx = 24.dp
        val offsetPx = screenWidth - pageMarginPx - pagerWidth
        with(binding.vpGifticon) {
            adapter = gifticonAdapter
            offscreenPageLimit = 3
            setPageTransformer { page, position ->
                page.translationX = position * -offsetPx
            }
            addItemDecoration(
                ListSpaceItemDecoration(
                    space = 48.dp,
                    start = 24.dp,
                    end = 24.dp
                )
            )
        }
    }

    private fun setInitAdapterData() {
        val focusModel = viewModel.focusMarker.value

        when (focusModel.captionText.isEmpty()) {
            true -> updateGifticonList(MarkerClickEvent.AllGifticon)
            false -> updateGifticonList(MarkerClickEvent.BrandGifticon(focusModel.captionText))
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    private fun updateGifticonList(markerClickEvent: MarkerClickEvent) {
        val couponList = viewModel.gifticonTestData.filter { gifticon ->
            when (markerClickEvent) {
                is MarkerClickEvent.AllGifticon -> viewModel.brandList.contains(gifticon.brand)
                is MarkerClickEvent.BrandGifticon -> gifticon.brand == markerClickEvent.brandName
            }
        }
        gifticonAdapter.submitList(couponList)
    }

    override fun onMapReady(map: NaverMap) {
        fusedLocationSource = FusedLocationSource(this, PERMISSION_REQUEST_CODE)
        naverMap = map.apply {
            this.locationSource = fusedLocationSource
            setOnMapClickListener { _, _ ->
                updateGifticonList(MarkerClickEvent.AllGifticon)
                resetFocusMarker(viewModel.focusMarker.value)
            }
        }
        currentLocationButton.map = naverMap

        setObserveFocusMarker()
        setGifticonAdapter()
        setObserveSearchData()
        setNaverMapZoom()
        setObserverMarkerData()
        setNaverMapPolyLine()
    }

    private fun setObserveFocusMarker() {
        repeatOnStarted {
            viewModel.focusMarker.collectLatest { marker ->
                marker.iconTintColor = Color.BLUE
                marker.zIndex = 1
                val location = marker.position
                moveMapCamera(location.longitude, location.latitude)
            }
        }
    }

    private fun setGifticonAdapter() {
        // TODO 추후에는 collect 하는 방식으로 바뀌어야함
        binding.vpGifticon.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (isFocusing(gifticonAdapter.currentList[position].brand)) return
                val currentItem = gifticonAdapter.currentList[position].brand
                val brandInfos = viewModel.brandInfos
                findBrandPlaceInfo(brandInfos, currentItem)
            }

            /**
             * 하단 ViewPager2 PageChangeCallback 실행시 현재 위치에서 가장 가까운 데이터를 갖고 오는 로직
             * @param brandPlaceInfos 지도에 보이고 있는 브랜드들
             * @param brandName 찾고자하는 브랜드명
             */
            private fun findBrandPlaceInfo(brandPlaceInfos: Set<BrandPlaceInfoUiModel>, brandName: String) {
                client.lastLocation.addOnSuccessListener { currentLocation ->
                    val brandPlaceInfo = brandPlaceInfos.filter { brandPlaceInfo ->
                        brandPlaceInfo.brand == brandName
                    }.minByOrNull { location ->
                        diffLocation(location, currentLocation)
                    } ?: return@addOnSuccessListener

                    resetFocusMarker(viewModel.focusMarker.value)
                    moveMapCamera(brandPlaceInfo.x.toDouble(), brandPlaceInfo.y.toDouble())

                    val currentFocusMarker = viewModel.markerHolder.find {
                        currentLocation(it, brandPlaceInfo)
                    } ?: return@addOnSuccessListener

                    viewModel.updateFocusMarker(currentFocusMarker)
                }
            }

            private fun currentLocation(it: Marker, brandPlaceInfo: BrandPlaceInfoUiModel) =
                it.position.longitude == brandPlaceInfo.x.toDouble() && it.position.latitude == brandPlaceInfo.y.toDouble()

            private fun diffLocation(
                location: BrandPlaceInfoUiModel,
                currentLocation: Location
            ) = LocationConverter.locationDistance(
                location.x.toDouble(),
                location.y.toDouble(),
                currentLocation.longitude,
                currentLocation.latitude
            )
        })
    }

    private fun isFocusing(brand: String) = viewModel.focusMarker.value.captionText == brand

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
                    is UiState.Loading -> Unit // TODO 로딩화면 처리 필요
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

    private fun setObserverMarkerData() {
        repeatOnStarted {
            viewModel.markers.collect { markers ->
                markers.forEach { it.map = naverMap }
            }
        }
        viewModel.updateBrandList()
    }

    private fun updateBrandMarker(brandPlaceSearchResults: List<BrandPlaceInfoUiModel>) {
        val brandMarkers = brandPlaceSearchResults.map { brandPlaceSearchResult ->
            Marker().apply {
                val latLng = LatLng(brandPlaceSearchResult.y.toDouble(), brandPlaceSearchResult.x.toDouble())
                setMarker(this, latLng, brandPlaceSearchResult)
            }
        }
        viewModel.updateMarkers(brandMarkers)
    }

    private fun setMarker(
        marker: Marker,
        latLng: LatLng,
        brandPlaceSearchResult: BrandPlaceInfoUiModel
    ) {
        with(marker) {
            position = latLng
            width = Marker.SIZE_AUTO
            height = Marker.SIZE_AUTO
            tag = brandPlaceSearchResult.placeUrl
            captionText = brandPlaceSearchResult.brand
            isHideCollidedSymbols = true
            zIndex = 0
        }
        marker.setOnClickListener {
            if (isSameMarker(marker)) return@setOnClickListener true
            val curFocusBrand = viewModel.focusMarker.value
            resetFocusMarker(curFocusBrand)
            viewModel.updateFocusMarker(marker)
            updateGifticonList(MarkerClickEvent.BrandGifticon(marker.captionText))
            true
        }
    }

    private fun isSameMarker(marker: Marker) =
        marker.position.longitude == viewModel.focusMarker.value.position.longitude &&
            marker.position.latitude == viewModel.focusMarker.value.position.latitude

    private fun resetFocusMarker(marker: Marker) {
        marker.zIndex = 0
        marker.icon = DEFAULT_ICON
        marker.iconTintColor = Color.TRANSPARENT
    }

    private fun showSnackBar(@StringRes message: Int) {
        Snackbar.make(binding.layoutMap, message, Snackbar.LENGTH_SHORT).show()
    }

    // TODO 릴리즈 단계에서는 사라져야할 함수입니다.
    private val polygonOverlay = PolygonOverlay()
    private fun setNaverMapPolyLine() {
        repeatOnStarted {
            viewModel.userLocation.collect {
                polygonOverlay.map = null
                val x = it.first
                val y = it.second
                val toPolygonLatLng = toPolygonLatLng(x, y)

                polygonOverlay.coords = listOf(
                    LatLng(toPolygonLatLng[0].second, toPolygonLatLng[0].first),
                    LatLng(toPolygonLatLng[1].second, toPolygonLatLng[1].first),
                    LatLng(toPolygonLatLng[2].second, toPolygonLatLng[2].first),
                    LatLng(toPolygonLatLng[3].second, toPolygonLatLng[3].first)
                )
                polygonOverlay.color = getColor(R.color.polygon)
                polygonOverlay.map = naverMap
            }
        }
    }
    // TODO 릴리즈 단계에서는 사라져야할 함수입니다.

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
