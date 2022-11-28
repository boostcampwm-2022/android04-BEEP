package com.lighthouse.presentation.ui.detailgifticon

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.ActivityGifticonDetailBinding
import com.lighthouse.presentation.databinding.DialogUsageHistoryBinding
import com.lighthouse.presentation.extension.isOnScreen
import com.lighthouse.presentation.extension.repeatOnStarted
import com.lighthouse.presentation.extension.scrollToBottom
import com.lighthouse.presentation.ui.common.dialog.SpinnerDatePicker
import com.lighthouse.presentation.ui.detailgifticon.dialog.UsageHistoryAdapter
import com.lighthouse.presentation.ui.detailgifticon.dialog.UseGifticonDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date

@AndroidEntryPoint
class GifticonDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGifticonDetailBinding
    private val viewModel: GifticonDetailViewModel by viewModels()

    private val standardGifticonInfo by lazy { StandardGifticonInfoFragment() }
    private val cashCardGifticonInfo by lazy { CashCardGifticonInfoFragment() }

    private lateinit var checkEditDialog: AlertDialog
    private lateinit var usageHistoryDialog: AlertDialog
    private lateinit var useGifticonDialog: UseGifticonDialog
    private lateinit var gifticonInfoChangedSnackbar: Snackbar
    private lateinit var gifticonInfoNotChangedToast: Toast

    private val usageHistoryAdapter by lazy { UsageHistoryAdapter() }

    private val btnUseGifticon by lazy { binding.btnUseGifticon }
    private val chip by lazy { binding.chipScrollDownForUseButton }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_gifticon_detail)
        binding.lifecycleOwner = this
        binding.vm = viewModel

        setSupportActionBar(binding.tbGifticonDetail)

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
            viewModel.gifticon.collect { gifticon ->
                gifticon?.let {
                    if (it.isCashCard) {
                        supportFragmentManager.commit {
                            replace(binding.fcvGifticonInfo.id, cashCardGifticonInfo)
                        }
                    } else {
                        supportFragmentManager.commit {
                            replace(binding.fcvGifticonInfo.id, standardGifticonInfo)
                        }
                    }
                }
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
        repeatOnStarted {
            viewModel.failure.collect {
                showInvalidDialog()
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
            is Event.OnGifticonInfoChanged -> {
                if (event.before == event.after) {
                    showGifticonInfoNotChangedToast()
                } else {
                    showGifticonInfoChangedSnackBar(event.before)
                }
            }
            is Event.ExpireDateClicked -> {
                showDatePickerDialog()
            }
            is Event.UseGifticonButtonClicked -> {
                // TODO 보안 인증
                showUseGifticonDialog()
            }
            is Event.ShowAllUsedInfoButtonClicked -> {
                showUsageHistoryDialog()
            }
            else -> { // TODO(이벤트 처리)
            }
        }
    }

    private fun showCheckEditDialog() {
        if (::checkEditDialog.isInitialized.not()) {
            checkEditDialog = AlertDialog.Builder(this)
                .setTitle(getString(R.string.gifticon_detail_check_edit_dialog_title))
                .setPositiveButton(getString(R.string.gifticon_detail_check_edit_dialog_positive_button)) { _, _ ->
                    viewModel.switchMode(GifticonDetailMode.EDIT)
                    viewModel.startEdit()
                }
                .setNegativeButton(getString(R.string.gifticon_detail_check_edit_dialog_negative_button)) { dialog, _ ->
                    dialog.cancel()
                }
                .create()
        }
        checkEditDialog.show()
    }

    private fun showDatePickerDialog() {
        SpinnerDatePicker(
            this,
            viewModel.gifticon.value?.expireAt ?: Date()
        ) { picker, year, month, dayOfMonth ->
            viewModel.editExpireDate(year, month, dayOfMonth)
            picker.dismiss()
        }.show()
    }

    private fun showUseGifticonDialog() {
        useGifticonDialog = UseGifticonDialog().also { dialog ->
            dialog.show(supportFragmentManager, UseGifticonDialog.TAG)
        }
    }

    private fun showUsageHistoryDialog() {
        if (::usageHistoryDialog.isInitialized.not()) {
            val usageHistoryView = DataBindingUtil.inflate<DialogUsageHistoryBinding>(
                LayoutInflater.from(this),
                R.layout.dialog_usage_history,
                null,
                false
            )
            usageHistoryView.vm = viewModel
            usageHistoryView.rvUsageHistory.adapter = usageHistoryAdapter
            Timber.tag("usageHistory").d("${usageHistoryAdapter.currentList}")
            usageHistoryDialog = AlertDialog.Builder(this).setView(usageHistoryView.root).create()
        }

        usageHistoryDialog.show()
    }

    private fun showInvalidDialog() {
        var lastSecond = INVALID_DIALOG_DEADLINE_SECOND // 자동으로 닫힐 시간(초)
        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.gifticon_detail_invalid_dialog_title))
            .setNegativeButton(getString(R.string.all_close_button)) { dialog, _ ->
                dialog.dismiss()
            }
            .setOnDismissListener {
                finish()
            }
            .create()
        lifecycleScope.launch {
            do {
                dialog
                    .setMessage(getString(R.string.gifticon_detail_invalid_dialog_message, lastSecond))
                dialog.show()
                delay(1000)
            } while (--lastSecond > 0)

            dialog.dismiss()
            cancel()
        }
    }

    private fun showGifticonInfoChangedSnackBar(before: Gifticon) {
        if (::gifticonInfoChangedSnackbar.isInitialized.not()) {
            gifticonInfoChangedSnackbar = Snackbar.make(
                binding.clGifticonDetail,
                getString(R.string.gifticon_detail_info_changed_snackbar_text),
                INFO_CHANGED_SNACKBAR_DURATION_MILLI_SECOND
            ).apply {
                animationMode = Snackbar.ANIMATION_MODE_SLIDE
            }
        }
        gifticonInfoChangedSnackbar.setAction(getString(R.string.gifticon_detail_info_changed_snackbar_action_text)) {
            viewModel.rollbackChangedGifticonInfo(before)
        }.also { snackbar ->
            snackbar.show()
        }
    }

    private fun showGifticonInfoNotChangedToast() {
        if (::gifticonInfoNotChangedToast.isInitialized.not()) {
            gifticonInfoNotChangedToast = Toast.makeText(this, "변경된 내용이 없습니다", Toast.LENGTH_SHORT)
        }
        gifticonInfoNotChangedToast.show()
    }

    companion object {
        const val INVALID_DIALOG_DEADLINE_SECOND = 5
        const val INFO_CHANGED_SNACKBAR_DURATION_MILLI_SECOND = 5000
    }
}
