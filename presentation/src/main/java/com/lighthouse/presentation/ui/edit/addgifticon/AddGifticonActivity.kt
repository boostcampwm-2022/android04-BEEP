package com.lighthouse.presentation.ui.edit.addgifticon

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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.ActivityAddGifticonBinding
import com.lighthouse.presentation.extension.getParcelable
import com.lighthouse.presentation.extension.getParcelableArrayList
import com.lighthouse.presentation.extension.repeatOnStarted
import com.lighthouse.presentation.extension.show
import com.lighthouse.presentation.extra.Extras
import com.lighthouse.presentation.model.CroppedImage
import com.lighthouse.presentation.model.GalleryUIModel
import com.lighthouse.presentation.ui.common.dialog.ConfirmationDialog
import com.lighthouse.presentation.ui.common.dialog.OriginImageDialog
import com.lighthouse.presentation.ui.common.dialog.ProgressDialog
import com.lighthouse.presentation.ui.common.dialog.datepicker.SpinnerDatePicker
import com.lighthouse.presentation.ui.cropgifticon.CropGifticonActivity
import com.lighthouse.presentation.ui.cropgifticon.CropGifticonParams
import com.lighthouse.presentation.ui.cropgifticon.view.CropImageInfo
import com.lighthouse.presentation.ui.cropgifticon.view.CropImageMode
import com.lighthouse.presentation.ui.edit.addgifticon.adapter.AddGifticonAdapter
import com.lighthouse.presentation.ui.edit.addgifticon.adapter.AddGifticonItemUIModel
import com.lighthouse.presentation.ui.edit.addgifticon.event.AddGifticonCrop
import com.lighthouse.presentation.ui.edit.addgifticon.event.AddGifticonEvent
import com.lighthouse.presentation.ui.edit.addgifticon.event.AddGifticonTag
import com.lighthouse.presentation.ui.gallery.GalleryActivity
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
class AddGifticonActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddGifticonBinding

    private val viewModel: AddGifticonViewModel by viewModels()

    private val gifticonAdapter = AddGifticonAdapter(
        onClickGallery = {
            viewModel.gotoGallery()
        },
        onClickGifticon = { gifticon ->
            viewModel.selectGifticon(gifticon)
        },
        onDeleteGifticon = { gifticon ->
            viewModel.showDeleteConfirmation(gifticon)
        },
    )

    private val gallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val list = result.data?.getParcelableArrayList(
                    Extras.KEY_SELECTED_GALLERY_ITEM,
                    GalleryUIModel.Gallery::class.java,
                ) ?: emptyList()
                viewModel.loadGalleryImages(list)
            }
        }

    private fun getCropResult(result: ActivityResult): CroppedImage? {
        val croppedUri =
            result.data?.getParcelable(Extras.KEY_CROPPED_IMAGE, Uri::class.java) ?: return null
        val croppedRect =
            result.data?.getParcelable(Extras.KEY_CROPPED_RECT, RectF::class.java) ?: return null
        return CroppedImage(croppedUri, croppedRect)
    }

    private suspend fun getCropResult(result: ActivityResult, id: Long): CroppedImage? {
        return withContext(Dispatchers.IO) {
            val outputFile = getFileStreamPath("$TEMP_GIFTICON_PREFIX$id")
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

    private val cropGifticon =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            lifecycleScope.launch {
                val gifticon = viewModel.selectedGifticon.value ?: return@launch
                val croppedImage = getCropResult(result, gifticon.id) ?: return@launch
                viewModel.updateCroppedGifticonImage(croppedImage)
            }
        }

    private val cropGifticonName =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            viewModel.recognizeGifticonName(getCropResult(result))
        }

    private val cropBrandName =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            viewModel.recognizeBrand(getCropResult(result))
        }

    private val cropBarcode =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            viewModel.recognizeBarcode(getCropResult(result))
        }

    private val cropBalance =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            viewModel.recognizeBalance(getCropResult(result))
        }

    private val cropExpired =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            viewModel.recognizeExpired(getCropResult(result))
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_gifticon)
        binding.apply {
            lifecycleOwner = this@AddGifticonActivity
            vm = viewModel
        }

        setUpBackPressed()
        setUpRecyclerView()
        collectEvent()
    }

    private fun setUpBackPressed() {
        onBackPressedDispatcher.addCallback {
            viewModel.requestPopBackstack()
        }
    }

    private fun setUpRecyclerView() {
        binding.rvGifticon.apply {
            adapter = gifticonAdapter
        }
    }

    private fun collectEvent() {
        repeatOnStarted {
            viewModel.eventFlow.collect { event ->
                when (event) {
                    is AddGifticonEvent.PopupBackStack -> cancelAddGifticon()
                    is AddGifticonEvent.ShowCancelConfirmation -> showConfirmationCancelDialog()
                    is AddGifticonEvent.ShowDeleteConfirmation -> showConfirmationDeleteDialog(event.gifticon)
                    is AddGifticonEvent.NavigateToGallery -> gotoGallery(event.list)
                    is AddGifticonEvent.NavigateToCrop -> gotoCrop(
                        event.crop,
                        event.origin,
                        event.croppedRect,
                    )

                    is AddGifticonEvent.ShowOriginGifticon -> showOriginGifticonDialog(event.origin)
                    is AddGifticonEvent.ShowExpiredAtDatePicker -> showExpiredAtDatePicker(event.date)
                    is AddGifticonEvent.RequestLoading -> requestLoading(event.loading)
                    is AddGifticonEvent.RequestFocus -> requestFocus(event.tag)
                    is AddGifticonEvent.RequestScroll -> requestScroll(event.tag)
                    is AddGifticonEvent.ShowSnackBar -> showSnackBar(event.uiText)
                    is AddGifticonEvent.RegistrationCompleted -> completeAddGifticon()
                }
            }
        }
    }

    private fun cancelAddGifticon() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun completeAddGifticon() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun gotoGallery(list: List<GalleryUIModel.Gallery>) {
        val intent = Intent(this, GalleryActivity::class.java).apply {
            putParcelableArrayListExtra(
                Extras.KEY_SELECTED_GALLERY_ITEM,
                ArrayList<GalleryUIModel.Gallery>().apply {
                    addAll(list)
                },
            )
        }
        gallery.launch(intent)
    }

    private fun gotoCrop(crop: AddGifticonCrop, uri: Uri, croppedRect: RectF) {
        val gotoCropLauncher = when (crop) {
            AddGifticonCrop.GIFTICON_IMAGE -> cropGifticon
            AddGifticonCrop.GIFTICON_NAME -> cropGifticonName
            AddGifticonCrop.BRAND_NAME -> cropBrandName
            AddGifticonCrop.BARCODE -> cropBarcode
            AddGifticonCrop.BALANCE -> cropBalance
            AddGifticonCrop.EXPIRED -> cropExpired
        }
        val param = CropGifticonParams(
            CropImageInfo(uri, croppedRect),
            if (crop == AddGifticonCrop.GIFTICON_IMAGE) CropImageMode.DRAG_WINDOW else CropImageMode.DRAW_PEN,
            crop == AddGifticonCrop.GIFTICON_IMAGE,
        )

        val intent = Intent(this, CropGifticonActivity::class.java).apply {
            param.putExtra(this)
        }
        gotoCropLauncher.launch(intent)
    }

    private var originImageDialog: OriginImageDialog? = null

    private fun showOriginGifticonDialog(uri: Uri) {
        if (originImageDialog?.isAdded == true) {
            originImageDialog?.dismiss()
        }
        originImageDialog = OriginImageDialog().apply {
            arguments = Bundle().apply {
                putParcelable(Extras.KEY_ORIGIN_IMAGE, uri)
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
        val title = getString(R.string.add_gifticon_confirmation_cancel_title)
        val message = getString(R.string.add_gifticon_confirmation_cancel_message)
        confirmationCancelDialog = ConfirmationDialog().apply {
            setTitle(title)
            setMessage(message)
            setOnOkClickListener {
                cancelAddGifticon()
            }
        }
        confirmationCancelDialog?.show(supportFragmentManager, CONFIRMATION_CANCEL_DIALOG)
    }

    private var confirmationDeleteDialog: ConfirmationDialog? = null

    private fun showConfirmationDeleteDialog(gifticon: AddGifticonItemUIModel.Gifticon) {
        if (confirmationDeleteDialog?.isAdded == true) {
            confirmationDeleteDialog?.dismiss()
        }
        val title = getString(R.string.add_gifticon_confirmation_delete_title)
        confirmationDeleteDialog = ConfirmationDialog().apply {
            setTitle(title)
            setOnOkClickListener {
                viewModel.deleteGifticon(gifticon)
            }
        }
        confirmationDeleteDialog?.show(supportFragmentManager, CONFIRMATION_DELETE_DIALOG)
    }

    private var progressDialog: ProgressDialog? = null

    private fun requestLoading(loading: Boolean) {
        if (progressDialog?.isAdded == true) {
            progressDialog?.dismiss()
        }
        progressDialog = if (loading) ProgressDialog() else null
        progressDialog?.show(supportFragmentManager)
    }

    private fun requestFocus(tag: AddGifticonTag) {
        val focusView = when (tag) {
            AddGifticonTag.GIFTICON_NAME -> binding.tietName
            AddGifticonTag.BRAND_NAME -> binding.tietBrand
            AddGifticonTag.BARCODE -> binding.tietBarcode
            AddGifticonTag.BALANCE -> binding.tietBalance
            else -> binding.clContainer
        }
        focusView.requestFocus()
        val inputMethodService =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (tag.needKeyboard) {
            inputMethodService.showSoftInput(focusView, 0)
        } else {
            inputMethodService.hideSoftInputFromWindow(
                currentFocus?.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS,
            )
        }
    }

    private fun requestScroll(tag: AddGifticonTag) {
        val focusView = when (tag) {
            AddGifticonTag.GIFTICON_NAME -> binding.tvName
            AddGifticonTag.BRAND_NAME -> binding.tvBrand
            AddGifticonTag.APPROVE_BRAND_NAME -> binding.tvApproveBrandNameDescription
            AddGifticonTag.BARCODE -> binding.tvBarcode
            AddGifticonTag.BALANCE -> binding.tvBalance
            AddGifticonTag.APPROVE_GIFTICON_IMAGE -> binding.ivGifticonImage
            else -> null
        } ?: return

        val dir = if (binding.nsv.scrollY - focusView.top > 0) SCROLL_DIR_DOWN else SCROLL_DIR_UP
        if (binding.nsv.canScrollVertically(dir)) {
            binding.nsv.smoothScrollTo(0, focusView.top)
        }
    }

    private fun showSnackBar(uiText: UIText) {
        Snackbar.make(binding.root, uiText.asString(applicationContext), Snackbar.LENGTH_SHORT)
            .show()
    }

    companion object {
        private const val SCROLL_DIR_UP = 1
        private const val SCROLL_DIR_DOWN = -1

        private const val CONFIRMATION_CANCEL_DIALOG = "Tag.ConfirmationCancelDialog"
        private const val CONFIRMATION_DELETE_DIALOG = "Tag.ConfirmationDeleteDialog"

        private const val TEMP_GIFTICON_PREFIX = "temp_gifticon_"
    }
}
