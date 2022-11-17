package com.lighthouse.presentation.ui.detailgifticon

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.ActivityGifticonDetailBinding
import com.lighthouse.presentation.extension.isOnScreen
import com.lighthouse.presentation.extension.repeatOnStarted
import com.lighthouse.presentation.extension.scrollToBottom
import com.lighthouse.presentation.ui.common.dialog.CustomSpinnerDatePicker
import com.lighthouse.presentation.ui.detailgifticon.dialog.UseGifticonDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GifticonDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGifticonDetailBinding
    private val viewModel: GifticonDetailViewModel by viewModels()

    private lateinit var checkEditDialog: AlertDialog
    private lateinit var useGifticonDialog: UseGifticonDialog

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
            }
        }
        repeatOnStarted {
            viewModel.event.collect { event ->
                handleEvent(event)
            }
        }
        repeatOnStarted {
            viewModel.mode.collect { mode ->
                when (mode) {
                    GifticonDetailMode.UNUSED -> {
                        binding.btnUseGifticon.text = getString(R.string.gifticon_detail_unused_mode_button_text)
                    }
                    GifticonDetailMode.USED -> {
                        binding.btnUseGifticon.text = getString(R.string.gifticon_detail_used_mode_button_text)
                    }
                    GifticonDetailMode.EDIT -> {
                        binding.btnUseGifticon.text = getString(R.string.gifticon_detail_edit_mode_button_text)
                    }
                }
            }
        }
    }

    private fun handleEvent(event: Event) {
        when (event) {
            is Event.ScrollDownForUseButtonClicked -> {
                binding.svGifticonDetail.scrollToBottom()
            }
            is Event.EditButtonClicked -> {
                showCheckEditDialog()
            }
            is Event.ExpireDateClicked -> {
                showDatePickerDialog()
            }
            is Event.UseGifticonButtonClicked -> {
                // TODO 보안 인증
                showUseGifticonDialog()
            }
            else -> { // TODO(이벤트 처리)
            }
        }
    }

    private fun showCheckEditDialog() {
        if (this::checkEditDialog.isInitialized.not()) {
            checkEditDialog = AlertDialog.Builder(this)
                .setTitle(getString(R.string.gifticon_detail_check_edit_dialog_title))
                .setPositiveButton(getString(R.string.gifticon_detail_check_edit_dialog_positive_button)) { _, _ ->
                    viewModel.switchMode(GifticonDetailMode.EDIT)
                }
                .setNegativeButton(getString(R.string.gifticon_detail_check_edit_dialog_negative_button)) { dialog, _ ->
                    dialog.cancel()
                }
                .create()
        }
        checkEditDialog.show()
    }

    private fun showDatePickerDialog() {
        CustomSpinnerDatePicker(this) { picker, year, month, dayOfMonth ->
            binding.tvExpireDate.text = getString(R.string.all_date, year, month, dayOfMonth)
            picker.dismiss()
        }.apply {
            setDate(viewModel.gifticon.value.expireAt)
        }
            .create()
            .show()
    }

    private fun showUseGifticonDialog() {
        useGifticonDialog = UseGifticonDialog().also { dialog ->
            dialog.show(supportFragmentManager, UseGifticonDialog.TAG)
        }
    }
}
