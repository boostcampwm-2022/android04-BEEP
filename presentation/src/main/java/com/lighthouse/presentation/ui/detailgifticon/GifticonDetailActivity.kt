package com.lighthouse.presentation.ui.detailgifticon

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.ActivityGifticonDetailBinding
import com.lighthouse.presentation.databinding.DialogUsageHistoryBinding
import com.lighthouse.presentation.extension.isOnScreen
import com.lighthouse.presentation.extension.repeatOnStarted
import com.lighthouse.presentation.extension.scrollToBottom
import com.lighthouse.presentation.ui.common.dialog.SpinnerDatePicker
import com.lighthouse.presentation.ui.detailgifticon.dialog.UsageHistoryAdapter
import com.lighthouse.presentation.ui.detailgifticon.dialog.UseGifticonDialog
import com.lighthouse.presentation.ui.security.AuthCallback
import com.lighthouse.presentation.ui.security.AuthManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class GifticonDetailActivity : AppCompatActivity(), AuthCallback {

    private lateinit var binding: ActivityGifticonDetailBinding
    private val viewModel: GifticonDetailViewModel by viewModels()

    private lateinit var checkEditDialog: AlertDialog
    private lateinit var usageHistoryDialog: AlertDialog
    private lateinit var useGifticonDialog: UseGifticonDialog

    private val usageHistoryAdapter by lazy { UsageHistoryAdapter() }

    private val btnUseGifticon by lazy { binding.btnUseGifticon }
    private val chip by lazy { binding.chipScrollDownForUseButton }

    @Inject
    lateinit var authManager: AuthManager
    private val biometricLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> onAuthSuccess()
                else -> onAuthError()
            }
        }

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
            is Event.ExpireDateClicked -> {
                showDatePickerDialog()
            }
            is Event.UseGifticonButtonClicked -> {
                authManager.auth(this, biometricLauncher, this)
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
            this
        ) { picker, year, month, dayOfMonth ->
            binding.tvExpireDate.text = getString(R.string.all_date, year, month, dayOfMonth)
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

    override fun onAuthSuccess() {
        Timber.tag("Auth").d("onAuthSuccess")
        showUseGifticonDialog()
    }

    override fun onAuthCancel() {
        Timber.tag("Auth").d("onAuthCancel")
    }

    override fun onAuthError() {
        Timber.tag("Auth").d("onAuthError")
    }

    companion object {
        const val INVALID_DIALOG_DEADLINE_SECOND = 5
    }
}
