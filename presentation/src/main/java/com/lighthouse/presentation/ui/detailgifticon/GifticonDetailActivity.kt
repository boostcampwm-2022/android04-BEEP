package com.lighthouse.presentation.ui.detailgifticon

import android.app.Activity
import android.content.Intent
import android.graphics.RectF
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toRect
import androidx.core.graphics.toRectF
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.domain.model.GifticonCrop
import com.lighthouse.presentation.R
import com.lighthouse.presentation.binding.loadUriWithoutCache
import com.lighthouse.presentation.databinding.ActivityGifticonDetailBinding
import com.lighthouse.presentation.databinding.DialogUsageHistoryBinding
import com.lighthouse.presentation.extension.getParcelable
import com.lighthouse.presentation.extension.isOnScreen
import com.lighthouse.presentation.extension.repeatOnStarted
import com.lighthouse.presentation.extension.scrollToBottom
import com.lighthouse.presentation.extension.show
import com.lighthouse.presentation.extra.Extras
import com.lighthouse.presentation.mapper.toDomain
import com.lighthouse.presentation.model.CroppedImage
import com.lighthouse.presentation.ui.addgifticon.dialog.OriginImageDialog
import com.lighthouse.presentation.ui.common.dialog.datepicker.SpinnerDatePicker
import com.lighthouse.presentation.ui.cropgifticon.CropGifticonActivity
import com.lighthouse.presentation.ui.detailgifticon.dialog.LargeBarcodeDialog
import com.lighthouse.presentation.ui.detailgifticon.dialog.UsageHistoryAdapter
import com.lighthouse.presentation.ui.detailgifticon.dialog.UseGifticonDialog
import com.lighthouse.presentation.ui.security.AuthCallback
import com.lighthouse.presentation.ui.security.AuthManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.inject.Inject

@AndroidEntryPoint
class GifticonDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGifticonDetailBinding
    private val viewModel: GifticonDetailViewModel by viewModels()

    private val standardGifticonInfo by lazy {
        supportFragmentManager.findFragmentByTag(StandardGifticonInfoFragment::class.java.name)
            ?: StandardGifticonInfoFragment()
    }
    private val cashCardGifticonInfo by lazy {
        supportFragmentManager.findFragmentByTag(CashCardGifticonInfoFragment::class.java.name)
            ?: CashCardGifticonInfoFragment()
    }

    private lateinit var checkEditDialog: AlertDialog
    private lateinit var usageHistoryDialog: AlertDialog
    private lateinit var useGifticonDialog: UseGifticonDialog
    private lateinit var gifticonInfoNotChangedToast: Toast
    private val usageHistoryAdapter by lazy { UsageHistoryAdapter() }

    private var largeBarcodeDialog: LargeBarcodeDialog? = null

    private val btnUseGifticon by lazy { binding.btnUseGifticon }
    private val chip by lazy { binding.chipScrollDownForUseButton }

    @Inject
    lateinit var authManager: AuthManager
    private val biometricLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> authenticate()
                else -> authCallback.onAuthError()
            }
        }

    private val authCallback = object : AuthCallback {
        override fun onAuthSuccess() {
            showUseGifticonDialog()
        }

        override fun onAuthCancel() {
        }

        override fun onAuthError(@StringRes stringId: Int?) {
            if (stringId != null) {
                Toast.makeText(this@GifticonDetailActivity, getString(stringId), Toast.LENGTH_SHORT).show()
            } else {
                authenticate()
            }
        }
    }

    private val cropGifticon = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val gifticon = viewModel.gifticon.value ?: return@registerForActivityResult
        val output = getFileStreamPath(gifticon.croppedPath)

        lifecycleScope.launch {
            val croppedImage = withContext(Dispatchers.IO) {
                getCropResult(result, output)
            } ?: return@launch
            viewModel.updateGifticonCrop(GifticonCrop(gifticon.id, croppedImage.croppedRect.toRect().toDomain()))
            binding.ivProductImage.loadUriWithoutCache(croppedImage.uri)
        }
    }

    private val backKeyCallback by lazy {
        onBackPressedDispatcher.addCallback {
            if (viewModel.mode.value == GifticonDetailMode.EDIT) {
                showNotSavedEditInfoDialog()
            } else {
                finish()
            }
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
            viewModel.gifticon.collect { gifticon ->
                val fragment = when (gifticon?.isCashCard) {
                    true -> cashCardGifticonInfo
                    false -> standardGifticonInfo
                    else -> null
                }
                if (fragment != null && fragment.isAdded.not()) {
                    supportFragmentManager.commit {
                        replace(binding.fcvGifticonInfo.id, fragment, fragment::class.java.name)
                    }
                }
                val output = getFileStreamPath(gifticon?.croppedPath ?: return@collect)
                binding.ivProductImage.loadUriWithoutCache(output.toUri())
            }
        }
        repeatOnStarted {
            viewModel.tempGifticon.collect { gifticon ->
                spinnerDatePicker.setDate(gifticon?.expireAt ?: return@collect)
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

    private fun handleEvent(event: GifticonDetailEvent) {
        when (event) {
            is GifticonDetailEvent.ScrollDownForUseButtonClicked -> {
                binding.abGifticonDetail.setExpanded(false, true)
                binding.svGifticonDetail.scrollToBottom()
            }
            is GifticonDetailEvent.EditButtonClicked -> {
                showCheckEditDialog()
            }
            is GifticonDetailEvent.ExistEmptyInfo -> {
                Toast.makeText(
                    this,
                    getString(R.string.gifticon_detail_exist_empty_info_toast),
                    Toast.LENGTH_SHORT
                ).show()
            }
            is GifticonDetailEvent.OnGifticonInfoChanged -> {
                if (event.before == event.after) {
                    showGifticonInfoNotChangedToast()
                } else {
                    showGifticonInfoChangedSnackBar(event.before)
                }
            }
            is GifticonDetailEvent.ExpireDateClicked -> {
                showDatePickerDialog()
            }
            is GifticonDetailEvent.UseGifticonButtonClicked -> {
                authenticate()
            }
            is GifticonDetailEvent.ShowAllUsedInfoButtonClicked -> {
                showUsageHistoryDialog()
            }
            is GifticonDetailEvent.UseGifticonComplete -> {
                if (::useGifticonDialog.isInitialized && useGifticonDialog.isAdded) {
                    useGifticonDialog.dismiss()
                }
            }
            is GifticonDetailEvent.ShowOriginalImage -> {
                showOriginGifticonDialog(event.origin)
            }
            is GifticonDetailEvent.ShowLargeBarcode -> {
                showLargeBarcodeDialog(event.barcode)
            }
            is GifticonDetailEvent.NavigateToCropGifticon -> {
                gotoCropGifticon(
                    getFileStreamPath(event.originPath).toUri(),
                    event.croppedRect.toRectF()
                )
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

    private val spinnerDatePicker = SpinnerDatePicker().apply {
        setOnDatePickListener { year, month, dayOfMonth ->
            viewModel.editExpireDate(year, month, dayOfMonth)
        }
    }

    private fun showNotSavedEditInfoDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.gifticon_detail_back_in_edit_dialog_title))
            .setMessage(getString(R.string.gifticon_detail_back_in_edit_dialog_message))
            .setPositiveButton(getString(R.string.all_do_cancel)) { dialog, _ ->
                viewModel.cancelEdit()
                dialog.dismiss()
            }
            .show()
    }

    private fun showDatePickerDialog() {
        spinnerDatePicker.show(supportFragmentManager)
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

    private fun authenticate() {
        authManager.auth(this, biometricLauncher, authCallback)
    }

    private fun showOriginGifticonDialog(path: String) {
        val uri = this.getFileStreamPath(path).toUri()
        OriginImageDialog().apply {
            arguments = Bundle().apply {
                putParcelable(Extras.KEY_ORIGIN_IMAGE, uri)
            }
        }.show(supportFragmentManager)
    }

    private fun showLargeBarcodeDialog(barcode: String) {
        if (largeBarcodeDialog?.isAdded == true) {
            largeBarcodeDialog?.dismiss()
        }
        largeBarcodeDialog = LargeBarcodeDialog().apply {
            arguments = Bundle().apply {
                putString(Extras.KEY_BARCODE, barcode)
            }
        }
        largeBarcodeDialog?.show(supportFragmentManager)
    }

    private suspend fun getCropResult(result: ActivityResult, output: File): CroppedImage? {
        return if (result.resultCode == Activity.RESULT_OK) {
            val croppedUri = result.data?.getParcelable(Extras.KEY_CROPPED_IMAGE, Uri::class.java) ?: return null
            val croppedRect = result.data?.getParcelable(Extras.KEY_CROPPED_RECT, RectF::class.java) ?: return null

            withContext(Dispatchers.IO) {
                FileInputStream(croppedUri.path).copyTo(
                    FileOutputStream(output)
                )
            }
            CroppedImage(output.toUri(), croppedRect)
        } else {
            null
        }
    }

    private fun gotoCropGifticon(uri: Uri, croppedRect: RectF) {
        val intent = Intent(this, CropGifticonActivity::class.java).apply {
            putExtra(Extras.KEY_ORIGIN_IMAGE, uri)
            putExtra(Extras.KEY_CROPPED_RECT, croppedRect)
        }
        cropGifticon.launch(intent)
    }

    private fun showGifticonInfoChangedSnackBar(before: Gifticon) {
        Snackbar.make(
            binding.clGifticonDetail,
            getString(R.string.gifticon_detail_info_changed_snackbar_text),
            INFO_CHANGED_SNACKBAR_DURATION_MILLI_SECOND
        ).apply {
            animationMode = Snackbar.ANIMATION_MODE_SLIDE
            setAction(getString(R.string.gifticon_detail_info_changed_snackbar_action_text)) {
                viewModel.rollbackChangedGifticonInfo(before)
            }
        }.show()
    }

    private fun showGifticonInfoNotChangedToast() {
        if (::gifticonInfoNotChangedToast.isInitialized.not()) {
            gifticonInfoNotChangedToast =
                Toast.makeText(this, getString(R.string.gifticon_detail_nothing_changed_toast), Toast.LENGTH_SHORT)
        }
        gifticonInfoNotChangedToast.show()
    }

    override fun onStart() {
        super.onStart()
        backKeyCallback.isEnabled = true
    }

    override fun onStop() {
        backKeyCallback.isEnabled = false
        super.onStop()
    }

    override fun onDestroy() {
        backKeyCallback.remove()
        super.onDestroy()
    }

    companion object {
        const val INVALID_DIALOG_DEADLINE_SECOND = 5
        const val INFO_CHANGED_SNACKBAR_DURATION_MILLI_SECOND = 5000
    }
}
