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
import com.lighthouse.presentation.extension.repeatOnStarted
import com.lighthouse.presentation.model.BrandPlaceInfoUiModel
import com.lighthouse.presentation.ui.common.UiState
import com.lighthouse.presentation.ui.map.adapter.MapGifticonAdapter
import com.lighthouse.presentation.ui.map.event.MarkerClickEvent
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
    private lateinit var locationSource: FusedLocationSource
    private val viewModel: MapViewModel by viewModels()
    private val adapter = MapGifticonAdapter()
    private val currentLocationButton: LocationButtonView by lazy { binding.btnCurrentLocation }
    private var focusMarker = Marker()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        client = LocationServices.getFusedLocationProviderClient(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_map)
        mapView = binding.mapView.apply {
            onCreate(savedInstanceState)
            getMapAsync(this@MapActivity)
        }
        setGifticonAdapter()
        setObserveSearchData()
        viewModel.collectLocation()
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
                    is UiState.Loading -> Unit // TODO 로딩화면 처리 필요
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

            setMarker(marker, latLng, brandPlaceSearchResult)
        }
    }

    private fun setMarker(
        marker: Marker,
        latLng: LatLng,
        brandPlaceSearchResult: BrandPlaceInfoUiModel
    ) {
        with(marker) {
            position = latLng
            map = naverMap
            width = Marker.SIZE_AUTO
            height = Marker.SIZE_AUTO
            tag = brandPlaceSearchResult.placeUrl
            captionText = brandPlaceSearchResult.brand

            setOnClickListener {
                resetFocusMarker()
                focusMarker = marker.apply {
                    iconTintColor = Color.RED
                }
                moveMapCamera(latLng.longitude, latLng.latitude)
                updateGifticonList(MarkerClickEvent.BrandGifticon(this.captionText))
                true
            }
        }
    }

    private fun resetFocusMarker() {
        focusMarker.icon = DEFAULT_ICON
        focusMarker.iconTintColor = Color.TRANSPARENT
    }

    private fun updateGifticonList(markerClickEvent: MarkerClickEvent) {
        val couponList = viewModel.gifticonTestData.filter { gifticon ->
            when (markerClickEvent) {
                is MarkerClickEvent.AllGifticon -> viewModel.brandList.contains(gifticon.brand)
                is MarkerClickEvent.BrandGifticon -> {
                    gifticon.brand == markerClickEvent.brandName
                }
            }
        }
        adapter.submitList(couponList)
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map
        locationSource = FusedLocationSource(this, PERMISSION_REQUEST_CODE)
        naverMap.locationSource = locationSource

        val uiSetting = naverMap.uiSettings
        uiSetting.isLocationButtonEnabled = false

        currentLocationButton.map = naverMap

        naverMap.setOnMapClickListener { _, _ ->
            resetFocusMarker()
            updateGifticonList(MarkerClickEvent.AllGifticon)
        }

        setNaverMapZoom()
        setNaverMapPolyLine()
    }

    private fun setNaverMapZoom() {
        naverMap.maxZoom = 18.0
        naverMap.minZoom = 7.0

        client.lastLocation.addOnSuccessListener { startLocation ->
            moveMapCamera(startLocation.longitude, startLocation.latitude)
        }
    }

    private fun setGifticonAdapter() {
        // TODO 추후에는 collect 하는 방식으로 바뀌어야함
        updateGifticonList(MarkerClickEvent.AllGifticon)
        binding.vpGifticon.adapter = adapter
        binding.vpGifticon.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val selectBrand = adapter.currentList[position].brand
                val brandInfos = viewModel.brandInfos
                findBrandPlaceInfo(brandInfos, selectBrand)
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
                    }.sortedBy { location ->
                        diffLocation(location, currentLocation)
                    }.toList().firstOrNull()

                    if (brandPlaceInfo != null) {
                        moveMapCamera(brandPlaceInfo.x.toDouble(), brandPlaceInfo.y.toDouble())
                    }
                }
            }

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

    private fun moveMapCamera(longitude: Double, latitude: Double) {
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(latitude, longitude))
            .animate(CameraAnimation.Easing)
        naverMap.moveCamera(cameraUpdate)
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
