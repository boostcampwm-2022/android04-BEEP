package com.lighthouse.presentation.ui.map

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.ActivityMapBinding
import com.lighthouse.presentation.extension.dp
import com.lighthouse.presentation.extension.screenWidth
import com.lighthouse.presentation.extra.Extras
import com.lighthouse.presentation.ui.common.GifticonViewHolderType
import com.lighthouse.presentation.ui.detailgifticon.GifticonDetailActivity
import com.lighthouse.presentation.ui.map.adapter.GifticonAdapter
import com.lighthouse.presentation.ui.map.prev.PrevMapActivity
import com.lighthouse.presentation.util.recycler.ListSpaceItemDecoration
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource
import dagger.hilt.android.AndroidEntryPoint

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

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        client = LocationServices.getFusedLocationProviderClient(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_map)
        binding.vm = viewModel
        binding.lifecycleOwner = this
        mapView = binding.mapView.apply {
            onCreate(savedInstanceState)
            getMapAsync(this@MapActivity)
        }

        setGifticonAdapterItem()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    private fun setGifticonAdapterItem() {
        val pageMargin = screenWidth * 0.1
        val pageWidth = screenWidth - 2 * pageMargin
        val offsetPx = screenWidth - pageMargin.toFloat() - pageWidth.toFloat()
        with(binding.vpGifticon) {
            adapter = gifticonAdapter
            offscreenPageLimit = 3
            setPageTransformer { page, position ->
                page.translationX = position * -offsetPx
            }
            addItemDecoration(
                ListSpaceItemDecoration(
                    space = 48.dp,
                    start = 20.dp,
                    end = 20.dp
                )
            )
        }
    }

    override fun onMapReady(p0: NaverMap) {
        fusedLocationSource = FusedLocationSource(this, PrevMapActivity.PERMISSION_REQUEST_CODE)
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
        private const val CATEGORY_MART = "대형마트"
        private const val CATEGORY_CONVENIENCE = "편의점"
        private const val CATEGORY_CULTURE = "문화시설"
        private const val CATEGORY_ACCOMMODATION = "숙박"
        private const val CATEGORY_RESTAURANT = "음식점"
        private const val CATEGORY_CAFE = "카페"
    }
}
