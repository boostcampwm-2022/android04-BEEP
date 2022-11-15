package com.lighthouse.presentation.ui.detailgifticon

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.ActivityGifticonDetailBinding
import com.lighthouse.presentation.extension.isOnScreen
import com.lighthouse.presentation.extension.repeatOnStarted
import com.lighthouse.presentation.extension.scrollToBottom

class GifticonDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGifticonDetailBinding
    private val viewModel: GifticonDetailViewModel by viewModels()

    private val btnUseGifticon by lazy { binding.btnUseGifticon }
    private val chip by lazy { binding.chipScrollDownForUseButton }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_gifticon_detail)
        binding.lifecycleOwner = this
        binding.vm = viewModel

        binding.btnUseGifticon.viewTreeObserver.addOnDrawListener {
            chip.post {
                chip.isVisible = btnUseGifticon.isOnScreen().not()
                chip.invalidate()
            }
        }
        repeatOnStarted {
            viewModel.event.collect { event ->
                handleEvent(event)
            }
        }
    }

    private fun handleEvent(event: Event) {
        when (event) {
            is Event.ScrollDownForUseButtonClicked -> {
                binding.svGifticonDetail.scrollToBottom()
            }
            else -> { // TODO(이벤트 처리)
            }
        }
    }
}
