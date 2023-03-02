package com.lighthouse.presentation.ui.edit.modifygifticon

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.RectF
import android.net.Uri
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.ActivityModifyGifticonBinding
import com.lighthouse.presentation.extension.getParcelable
import com.lighthouse.presentation.extension.repeatOnStarted
import com.lighthouse.presentation.extension.show
import com.lighthouse.presentation.extra.Extras
import com.lighthouse.presentation.model.CroppedImage
import com.lighthouse.presentation.ui.common.dialog.ConfirmationDialog
import com.lighthouse.presentation.ui.common.dialog.OriginImageDialog
import com.lighthouse.presentation.ui.common.dialog.datepicker.SpinnerDatePicker
import com.lighthouse.presentation.ui.cropgifticon.CropGifticonActivity
import com.lighthouse.presentation.ui.edit.modifygifticon.event.ModifyGifticonCrop
import com.lighthouse.presentation.ui.edit.modifygifticon.event.ModifyGifticonEvent
import com.lighthouse.presentation.ui.edit.modifygifticon.event.ModifyGifticonTag
import com.lighthouse.presentation.util.resource.UIText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class ModifyGifticonActivity : AppCompatActivity() {

    private lateinit var binding: ActivityModifyGifticonBinding

    private val viewModel: ModifyGifticonViewModel by viewModels()

    private fun getCropResult(result: ActivityResult): CroppedImage? {
        val croppedUri = result.data?.getParcelable(Extras.KEY_CROPPED_IMAGE, Uri::class.java) ?: return null
        val croppedRect = result.data?.getParcelable(Extras.KEY_CROPPED_RECT, RectF::class.java) ?: return null
        return CroppedImage(croppedUri, croppedRect)
    }

    private suspend fun getCropImageResult(result: ActivityResult): CroppedImage? {
        return withContext(Dispatchers.IO) {
            val outputFile = getFileStreamPath(GIFTICON_IMAGE_CROPPED)
            val imageResult = getCropResult(result)
            if (imageResult?.uri != null) {
                FileInputStream(imageResult.uri.path).use { input ->
                    FileOutputStream(outputFile).use { output ->
                        input.copyTo(output)
                    }
                }
                CroppedImage(outputFile.toUri(), imageResult.croppedRect)
            } else {
                null
            }
        }
    }

    private val cropGifticonImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            lifecycleScope.launch {
                viewModel.updateCroppedGifticonImage(getCropImageResult(result))
            }
        }

    private val cropGifticonName =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            viewModel.recognizeGifticonName(getCropResult(result))
        }

    private val cropBrandName = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        viewModel.recognizeBrand(getCropResult(result))
    }

    private val cropBarcode = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        viewModel.recognizeBarcode(getCropResult(result))
    }

    private val cropBalance = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        viewModel.recognizeBalance(getCropResult(result))
    }

    private val cropExpired = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        viewModel.recognizeGifticonExpired(getCropResult(result))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_modify_gifticon)
        binding.apply {
            lifecycleOwner = this@ModifyGifticonActivity
            vm = viewModel
        }

        setUpBackPressed()
        collectEvent()
    }

    private fun setUpBackPressed() {
        onBackPressedDispatcher.addCallback {
            viewModel.requestPopBackstack()
        }
    }

    private fun collectEvent() {
        repeatOnStarted {
            viewModel.eventFlow.collect { event ->
                when (event) {
                    is ModifyGifticonEvent.PopupBackStack -> cancelModifyGifticon()
                    is ModifyGifticonEvent.ShowCancelConfirmation -> showConfirmationCancelDialog()
                    is ModifyGifticonEvent.NavigateToCrop -> gotoCrop(
                        event.crop,
                        event.originFileName,
                        event.croppedRect,
                    )

                    is ModifyGifticonEvent.ShowOriginGifticon -> showOriginGifticonDialog(event.originFileName)
                    is ModifyGifticonEvent.ShowExpiredAtDatePicker -> showExpiredAtDatePicker(event.date)
                    is ModifyGifticonEvent.RequestFocus -> requestFocus(event.tag)
                    is ModifyGifticonEvent.RequestScroll -> requestScroll(event.tag)
                    is ModifyGifticonEvent.ShowSnackBar -> showSnackBar(event.uiText)
                    is ModifyGifticonEvent.ModifyGifticonType -> showTypeChangeAlertDialog { confirm ->
                        if (confirm) {
                            viewModel.completeModifyGifticon()
                            // TODO History 삭제
                        }
                    }

                    is ModifyGifticonEvent.ModifyCompleted -> completeModifyGifticon()
                }
            }
        }
    }

    private fun cancelModifyGifticon() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun completeModifyGifticon() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun gotoCrop(crop: ModifyGifticonCrop, originFileName: String, croppedRect: RectF) {
        val gotoCropLauncher = when (crop) {
            ModifyGifticonCrop.GIFTICON_IMAGE -> cropGifticonImage
            ModifyGifticonCrop.GIFTICON_NAME -> cropGifticonName
            ModifyGifticonCrop.BRAND_NAME -> cropBrandName
            ModifyGifticonCrop.BARCODE -> cropBarcode
            ModifyGifticonCrop.BALANCE -> cropBalance
            ModifyGifticonCrop.EXPIRED -> cropExpired
        }
        val originUri = getFileStreamPath(originFileName).toUri()
        val intent = Intent(this, CropGifticonActivity::class.java).apply {
            putExtra(Extras.KEY_ORIGIN_IMAGE, originUri)
            putExtra(Extras.KEY_CROPPED_RECT, croppedRect)
            putExtra(Extras.KEY_ENABLE_ASPECT_RATIO, crop == ModifyGifticonCrop.GIFTICON_IMAGE)
        }
        gotoCropLauncher.launch(intent)
    }

    private var originImageDialog: OriginImageDialog? = null

    private fun showOriginGifticonDialog(originFileName: String) {
        if (originImageDialog?.isAdded == true) {
            originImageDialog?.dismiss()
        }
        val originUri = getFileStreamPath(originFileName).toUri()
        originImageDialog = OriginImageDialog().apply {
            arguments = Bundle().apply {
                putParcelable(Extras.KEY_ORIGIN_IMAGE, originUri)
            }
        }
        originImageDialog?.show(supportFragmentManager)
    }

    private var spinnerDatePicker: SpinnerDatePicker? = null

    private fun showExpiredAtDatePicker(date: Date) {
        if (spinnerDatePicker?.isAdded == true) {
            spinnerDatePicker?.dismiss()
        }
        spinnerDatePicker = SpinnerDatePicker().apply {
            setDate(date)
            setOnDatePickListener { year, month, dayOfMonth ->
                val newDate = Calendar.getInstance(Locale.getDefault()).let {
                    it.set(year, month - 1, dayOfMonth)
                    it.time
                }
                viewModel.updateExpiredAt(newDate)
            }
        }
        spinnerDatePicker?.show(supportFragmentManager)
    }

    private var confirmationCancelDialog: ConfirmationDialog? = null

    private fun showConfirmationCancelDialog() {
        if (confirmationCancelDialog?.isAdded == true) {
            confirmationCancelDialog?.dismiss()
        }
        val title = getString(R.string.modify_gifticon_confirmation_cancel_title)
        val message = getString(R.string.modify_gifticon_confirmation_cancel_message)
        confirmationCancelDialog = ConfirmationDialog().apply {
            setTitle(title)
            setMessage(message)
            setOnOkClickListener {
                cancelModifyGifticon()
            }
        }
        confirmationCancelDialog?.show(supportFragmentManager)
    }

    private fun requestFocus(tag: ModifyGifticonTag) {
        val focusView = when (tag) {
            ModifyGifticonTag.GIFTICON_NAME -> binding.tietName
            ModifyGifticonTag.BRAND_NAME -> binding.tietBrand
            ModifyGifticonTag.BARCODE -> binding.tietBarcode
            ModifyGifticonTag.BALANCE -> binding.tietBalance
            else -> binding.clContainer
        }
        focusView.requestFocus()
        val inputMethodService = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (tag.needKeyboard) {
            inputMethodService.showSoftInput(focusView, 0)
        } else {
            inputMethodService.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    private fun requestScroll(tag: ModifyGifticonTag) {
        val focusView = when (tag) {
            ModifyGifticonTag.GIFTICON_NAME -> binding.tvName
            ModifyGifticonTag.BRAND_NAME -> binding.tvBrand
            ModifyGifticonTag.APPROVE_BRAND_NAME -> binding.tvApproveBrandNameDescription
            ModifyGifticonTag.BARCODE -> binding.tvBarcode
            ModifyGifticonTag.BALANCE -> binding.tvBalance
            else -> null
        } ?: return

        val dir = if (binding.nsv.scrollY - focusView.top > 0) SCROLL_DIR_DOWN else SCROLL_DIR_UP
        if (binding.nsv.canScrollVertically(dir)) {
            binding.nsv.smoothScrollTo(0, focusView.top)
        }
    }

    private fun showSnackBar(uiText: UIText) {
        Snackbar.make(binding.root, uiText.asString(applicationContext), Snackbar.LENGTH_SHORT).show()
    }

    private fun showTypeChangeAlertDialog(confirm: (Boolean) -> Unit) {
        val message = UIText.Builder()
            .appendDynamicString(getString(R.string.modify_gifticon_type_message_1))
            .spanOnTextColor(R.color.black)
            .appendDynamicString(getString(R.string.modify_gifticon_type_message_usage_history))
            .spanOnTextColor(R.color.beep_pink)
            .appendDynamicString(getString(R.string.modify_gifticon_type_message_2))
            .spanOnTextColor(R.color.black)
            .appendDynamicString(getString(R.string.modify_gifticon_type_message_balance_info))
            .spanOnTextColor(R.color.beep_pink)
            .appendDynamicString(getString(R.string.modify_gifticon_type_message_3))
            .spanOnTextColor(R.color.black)
            .build()
            .asString(this)

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.modify_gifticon_type_alert_title))
            .setIcon(R.drawable.ic_warning)
            .setMessage(message)
            .setPositiveButton(getString(R.string.confirmation_modify)) { _, _ ->
                confirm(true)
            }
            .setNeutralButton(R.string.confirmation_cancel) { _, _ ->
                confirm(false)
            }
            .show()
            .apply {
                getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(
                    ContextCompat.getColor(
                        this@ModifyGifticonActivity,
                        R.color.black_50,
                    ),
                )
            }
    }

    companion object {
        private const val SCROLL_DIR_UP = 1
        private const val SCROLL_DIR_DOWN = -1

        private const val GIFTICON_IMAGE_CROPPED = "gifticon_image_cropped.jpg"
    }
}
