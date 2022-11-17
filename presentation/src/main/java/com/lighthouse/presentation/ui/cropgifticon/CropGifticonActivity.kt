package com.lighthouse.presentation.ui.cropgifticon

import android.app.Activity
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.ActivityCropGifticonBinding
import com.lighthouse.presentation.extension.repeatOnStarted
import com.lighthouse.presentation.ui.cropgifticon.event.CropGifticonEvents
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CropGifticonActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCropGifticonBinding

    private val viewModel: CropGifticonViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_crop_gifticon)
        binding.apply {
            lifecycleOwner = this@CropGifticonActivity
            vm = viewModel
        }

        setUpOnBackPressed()
        collectEvent()
    }

    private fun setUpOnBackPressed() {
        onBackPressedDispatcher.addCallback {
            cancelCropImage()
        }
    }

    private fun collectEvent() {
        repeatOnStarted {
            viewModel.eventsFlow.collect { events ->
                when (events) {
                    CropGifticonEvents.COMPLETE -> completeCropImage()
                    CropGifticonEvents.CANCEL -> cancelCropImage()
                }
            }
        }
    }

    private fun completeCropImage() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun cancelCropImage() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}
