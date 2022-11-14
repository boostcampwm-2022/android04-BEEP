package com.lighthouse.presentation.ui.map

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.ActivityMapBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapBinding
    private val viewModel: MapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_map)

        setObserveSearchData()
    }

    private fun setObserveSearchData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { state ->
                    // TODO 지도에 그려줄 때 처리할 로직들
                    when (state) {
                        is MapState.Success -> {}
                        is MapState.Failure -> {}
                        is MapState.NetworkFailure -> {}
                        is MapState.NotFoundSearchResults -> {}
                        is MapState.Loading -> {}
                    }
                }
            }
        }
    }
}
