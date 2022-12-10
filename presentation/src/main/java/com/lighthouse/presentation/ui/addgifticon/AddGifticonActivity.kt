package com.lighthouse.presentation.ui.addgifticon

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
import com.lighthouse.presentation.extension.dp
import com.lighthouse.presentation.extension.getParcelable
import com.lighthouse.presentation.extension.getParcelableArrayList
import com.lighthouse.presentation.extension.repeatOnStarted
import com.lighthouse.presentation.extension.show
import com.lighthouse.presentation.extra.Extras
import com.lighthouse.presentation.model.CroppedImage
import com.lighthouse.presentation.model.GalleryUIModel
import com.lighthouse.presentation.ui.addgifticon.adapter.AddGifticonAdapter
import com.lighthouse.presentation.ui.addgifticon.adapter.AddGifticonItemUIModel
import com.lighthouse.presentation.ui.addgifticon.dialog.OriginImageDialog
import com.lighthouse.presentation.ui.addgifticon.event.AddGifticonCrop
import com.lighthouse.presentation.ui.addgifticon.event.AddGifticonEvent
import com.lighthouse.presentation.ui.addgifticon.event.AddGifticonTag
import com.lighthouse.presentation.ui.common.dialog.ConfirmationDialog
import com.lighthouse.presentation.ui.common.dialog.ProgressDialog
import com.lighthouse.presentation.ui.common.dialog.datepicker.SpinnerDatePicker
import com.lighthouse.presentation.ui.cropgifticon.CropGifticonActivity
import com.lighthouse.presentation.ui.gallery.GalleryActivity
import com.lighthouse.presentation.util.recycler.ListSpaceItemDecoration
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
        }
    )

    private val gallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val list = result.data?.getParcelableArrayList(
                Extras.KEY_SELECTED_GALLERY_ITEM,
                GalleryUIModel.Gallery::class.java
            ) ?: emptyList()
            viewModel.loadGalleryImages(list)
        }
    }

    private suspend fun getCropResult(result: ActivityResult): Uri? {
        return withContext(Dispatchers.IO) {
            result.data?.getParcelable(Extras.KEY_CROPPED_IMAGE, Uri::class.java)
        }
    }

    private suspend fun getCropResult(result: ActivityResult, id: Long): CroppedImage? {
        return withContext(Dispatchers.IO) {
            val output = getFileStreamPath("$TEMP_GIFTICON_PREFIX$id")

            if (result.resultCode == Activity.RESULT_OK) {
                val croppedUri =
                    result.data?.getParcelable(Extras.KEY_CROPPED_IMAGE, Uri::class.java) ?: return@withContext null
                val croppedRect =
                    result.data?.getParcelable(Extras.KEY_CROPPED_RECT, RectF::class.java) ?: return@withContext null

                FileInputStream(croppedUri.path).copyTo(FileOutputStream(output))
                CroppedImage(output.toUri(), croppedRect)
            } else {
                null
            }
        }
    }

    private val cropGifticon = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        lifecycleScope.launch {
            val gifticon = viewModel.selectedGifticon.value ?: return@launch
            val croppedImage = getCropResult(result, gifticon.id) ?: return@launch
            viewModel.updateCroppedGifticonImage(croppedImage)
        }
    }

    private val cropGifticonName =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            lifecycleScope.launch {
                viewModel.recognizeGifticonName(getCropResult(result))
            }
        }

    private val cropBrandName = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        lifecycleScope.launch {
            viewModel.recognizeBrand(getCropResult(result))
        }
    }

    private val cropBarcode = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        lifecycleScope.launch {
            viewModel.recognizeBarcode(getCropResult(result))
        }
    }

    private val cropBalance = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        lifecycleScope.launch {
            viewModel.recognizeBalance(getCropResult(result))
        }
    }

    private val cropExpired = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        lifecycleScope.launch {
            viewModel.recognizeExpired(getCropResult(result))
        }
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
            addItemDecoration(ListSpaceItemDecoration(4.dp, 32.dp, 0f, 32.dp, 0f))
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
                    is AddGifticonEvent.NavigateToCrop -> gotoCrop(event.crop, event.origin, event.croppedRect)
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
                }
            )
        }
        gallery.launch(intent)
    }

    private fun gotoCrop(crop: AddGifticonCrop, uri: Uri, croppedRect: RectF) {
        val launcher = when (crop) {
            AddGifticonCrop.GIFTICON_IMAGE -> cropGifticon
            AddGifticonCrop.GIFTICON_NAME -> cropGifticonName
            AddGifticonCrop.BRAND_NAME -> cropBrandName
            AddGifticonCrop.BARCODE -> cropBarcode
            AddGifticonCrop.BALANCE -> cropBalance
            AddGifticonCrop.EXPIRED -> cropExpired
        }
        val intent = Intent(this, CropGifticonActivity::class.java).apply {
            putExtra(Extras.KEY_ORIGIN_IMAGE, uri)
            putExtra(Extras.KEY_CROPPED_RECT, croppedRect)
            if (crop != AddGifticonCrop.GIFTICON_IMAGE) {
                putExtra(Extras.KEY_ENABLE_ASPECT_RATIO, false)
            }
        }
        launcher.launch(intent)
    }

    private fun showOriginGifticonDialog(uri: Uri) {
        OriginImageDialog().apply {
            arguments = Bundle().apply {
                putParcelable(Extras.KEY_ORIGIN_IMAGE, uri)
            }
        }.show(supportFragmentManager)
    }

    private fun showExpiredAtDatePicker(date: Date) {
        SpinnerDatePicker().apply {
            setOnDatePickListener { year, month, dayOfMonth ->
                val newDate = Calendar.getInstance(Locale.getDefault()).let {
                    it.set(year, month - 1, dayOfMonth)
                    it.time
                }
                viewModel.updateExpiredAt(newDate)
            }
        }.apply {
            setDate(date)
        }.show(supportFragmentManager)
    }

    private fun showConfirmationCancelDialog() {
        val title = getString(R.string.add_gifticon_confirmation_cancel_title)
        val message = getString(R.string.add_gifticon_confirmation_cancel_message)
        ConfirmationDialog().apply {
            setTitle(title)
            setMessage(message)
            setOnOkClickListener {
                cancelAddGifticon()
            }
        }.show(supportFragmentManager, CONFIRMATION_CANCEL_DIALOG)
    }

    private fun showConfirmationDeleteDialog(gifticon: AddGifticonItemUIModel.Gifticon) {
        val title = getString(R.string.add_gifticon_confirmation_delete_title)
        ConfirmationDialog().apply {
            setTitle(title)
        }.apply {
            setOnOkClickListener {
                viewModel.deleteGifticon(gifticon)
            }
        }.show(supportFragmentManager, CONFIRMATION_DELETE_DIALOG)
    }

    private var progressDialog: ProgressDialog? = null

    private fun requestLoading(loading: Boolean) {
        if (progressDialog?.isAdded == true) {
            progressDialog?.dismiss()
        }
        progressDialog = if (loading) {
            ProgressDialog().also {
                it.show(supportFragmentManager)
            }
        } else {
            null
        }
    }

    private fun requestFocus(tag: AddGifticonTag) {
        val focusView = when (tag) {
            AddGifticonTag.GIFTICON_NAME -> binding.tietName
            AddGifticonTag.BRAND_NAME -> binding.tietBrand
            AddGifticonTag.BRAND_CONFIRM -> binding.ivBrandConfirm
            AddGifticonTag.BARCODE -> binding.tietBarcode
            AddGifticonTag.BALANCE -> binding.tietBalance
            AddGifticonTag.NONE -> binding.clContainer
        }
        focusView.requestFocus()
        val inputMethodService = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (tag.needKeyboard) {
            inputMethodService.showSoftInput(focusView, 0)
        } else {
            inputMethodService.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    private fun requestScroll(tag: AddGifticonTag) {
        val focusView = when (tag) {
            AddGifticonTag.GIFTICON_NAME -> binding.tvName
            AddGifticonTag.BRAND_NAME -> binding.tvBrand
            AddGifticonTag.BRAND_CONFIRM -> binding.ivBrandConfirm
            AddGifticonTag.BARCODE -> binding.tvBarcode
            AddGifticonTag.BALANCE -> binding.tvBalance
            else -> null
        } ?: return

        val dir = if (binding.nsv.scrollY - focusView.top > 0) SCROLL_DIR_UP else SCROLL_DIR_DOWN
        if (binding.nsv.canScrollVertically(dir)) {
            binding.nsv.smoothScrollTo(0, focusView.top)
        }
    }

    private fun showSnackBar(uiText: UIText) {
        Snackbar.make(binding.root, uiText.asString(applicationContext), Snackbar.LENGTH_SHORT).show()
    }

    companion object {
        private const val SCROLL_DIR_UP = 1
        private const val SCROLL_DIR_DOWN = -1

        private const val CONFIRMATION_CANCEL_DIALOG = "Tag.ConfirmationCancelDialog"
        private const val CONFIRMATION_DELETE_DIALOG = "Tag.ConfirmationDeleteDialog"

        private const val TEMP_GIFTICON_PREFIX = "temp_gifticon_"
    }
}
