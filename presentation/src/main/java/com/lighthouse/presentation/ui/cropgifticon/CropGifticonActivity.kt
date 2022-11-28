package com.lighthouse.presentation.ui.cropgifticon

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.RectF
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.ActivityCropGifticonBinding
import com.lighthouse.presentation.extension.repeatOnStarted
import com.lighthouse.presentation.extra.Extras
import com.lighthouse.presentation.util.resource.UIText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileOutputStream

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
                    is CropGifticonEvent.PopupBackStack -> cancelCropImage()
                    is CropGifticonEvent.RequestCrop -> requestCropImage()
                    is CropGifticonEvent.CompleteCrop -> completeCropImage(events.croppedBitmap, events.croppedRect)
                    is CropGifticonEvent.ShowSnackBar -> showSnackBar(events.uiText)
                }
            }
        }
    }

    private fun showSnackBar(uiText: UIText) {
        Snackbar.make(binding.root, uiText.asString(applicationContext), Snackbar.LENGTH_SHORT).show()
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

    private fun completeCropImage(croppedBitmap: Bitmap, croppedRect: RectF) {
        lifecycleScope.launch {
            val file = getFileStreamPath(TEMP_FILE_PATH)
            file.delete()

            withContext(Dispatchers.IO) {
                FileOutputStream(file).use {
                    croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                }
            }

            val intent = Intent().apply {
                putExtra(Extras.KEY_CROPPED_IMAGE, file.toUri())
                putExtra(Extras.KEY_CROPPED_RECT, croppedRect)
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    companion object {
        private const val TEMP_FILE_PATH = "cropped.jpg"
    }
}
