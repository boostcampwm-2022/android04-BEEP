package com.lighthouse.presentation.ui.map

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.ActivityMapBinding

class MapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapBinding
    private lateinit var viewModel: MapViewModel // TODO by viewModels + Hilt 로 리팩토링 예정

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_map)
        viewModel = ViewModelProvider(this)[MapViewModel::class.java]
    }
}
