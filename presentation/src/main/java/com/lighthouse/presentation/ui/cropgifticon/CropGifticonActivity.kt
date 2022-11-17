package com.lighthouse.presentation.ui.cropgifticon

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.ActivityCropGifticonBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CropGifticonActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCropGifticonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_crop_gifticon)
        binding.apply {
            lifecycleOwner = this@CropGifticonActivity
        }
    }
}
