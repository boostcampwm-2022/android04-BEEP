package com.lighthouse.presentation.ui.cropgifticon

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.ActivityCropGifticonBinding
import com.lighthouse.presentation.extension.repeatOnStarted
import com.lighthouse.presentation.extra.Extras
import com.lighthouse.presentation.ui.cropgifticon.event.CropGifticonEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        collectMessage()
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
                    is CropGifticonEvent.Crop -> requestCropImage()
                    is CropGifticonEvent.Cancel -> cancelCropImage()
                    is CropGifticonEvent.Complete -> completeCropImage(events.bitmap)
                }
            }
        }
    }

    private fun collectMessage() {
        repeatOnStarted {
            viewModel.messageFlow.collect { message ->
                showSnackBar(message)
            }
        }
    }

    private fun showSnackBar(@StringRes resId: Int) {
        Snackbar.make(binding.root, resId, Snackbar.LENGTH_SHORT).show()
    }

    private fun requestCropImage() {
        lifecycleScope.launch {
            binding.cropImageView.cropImage()
        }
    }

    private fun cancelCropImage() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun completeCropImage(bitmap: Bitmap) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                openFileOutput(TEMP_FILE_PATH, Context.MODE_PRIVATE).use {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                }
            }
            val intent = Intent().apply {
                putExtra(Extras.CroppedImage, Uri.fromFile(getFileStreamPath(TEMP_FILE_PATH)))
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    companion object {
        private const val TEMP_FILE_PATH = "cropped.jpg"
    }
}
