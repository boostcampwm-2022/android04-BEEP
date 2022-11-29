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
import com.lighthouse.presentation.extra.Extras
import com.lighthouse.presentation.model.CroppedImage
import com.lighthouse.presentation.model.GalleryUIModel
import com.lighthouse.presentation.ui.addgifticon.adapter.AddGifticonAdapter
import com.lighthouse.presentation.ui.addgifticon.adapter.AddGifticonItemUIModel
import com.lighthouse.presentation.ui.addgifticon.dialog.OriginImageDialog
import com.lighthouse.presentation.ui.common.dialog.ConfirmationDialog
import com.lighthouse.presentation.ui.common.dialog.datepicker.SpinnerDatePicker
import com.lighthouse.presentation.ui.cropgifticon.CropGifticonActivity
import com.lighthouse.presentation.ui.gallery.GalleryActivity
import com.lighthouse.presentation.util.recycler.ListSpaceItemDecoration
import com.lighthouse.presentation.util.resource.UIText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
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

    private val cropGifticon = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val gifticon = viewModel.selectedGifticon.value ?: return@registerForActivityResult
        val output = getFileStreamPath("$TEMP_GIFTICON_PREFIX${gifticon.id}")

        lifecycleScope.launch {
            val croppedImage = withContext(Dispatchers.IO) {
                getCropResult(result, output)
            } ?: return@launch
            viewModel.croppedGifticonImage(croppedImage)
        }
    }

    private val originImageDialog = OriginImageDialog()

    private val expiredAtDatePickerDialog by lazy {
        SpinnerDatePicker().apply {
            setOnDatePickListener { year, month, dayOfMonth ->
                val date = Calendar.getInstance(Locale.getDefault()).let {
                    it.set(year, month - 1, dayOfMonth)
                    it.time
                }
                viewModel.changeExpiredAt(date)
            }
        }
    }

    private val confirmationCancelDialog by lazy {
        val title = getString(R.string.add_gifticon_confirmation_cancel_title)
        val message = getString(R.string.add_gifticon_confirmation_cancel_message)
        ConfirmationDialog().apply {
            setTitle(title)
            setMessage(message)
            setOnOkClickListener {
                cancelAddGifticon()
            }
        }
    }

    private val confirmationDeleteDialog by lazy {
        val title = getString(R.string.add_gifticon_confirmation_delete_title)
        ConfirmationDialog().apply {
            setTitle(title)
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
            viewModel.eventFlow.collect { events ->
                when (events) {
                    is AddGifticonEvent.PopupBackStack -> cancelAddGifticon()
                    is AddGifticonEvent.ShowCancelConfirmation -> showConfirmationCancelDialog()
                    is AddGifticonEvent.ShowDeleteConfirmation -> showConfirmationDeleteDialog(events.gifticon)
                    is AddGifticonEvent.NavigateToGallery -> gotoGallery(events.list)
                    is AddGifticonEvent.NavigateToCropGifticon -> gotoCropGifticon(events.origin, events.croppedRect)
                    is AddGifticonEvent.ShowOriginGifticon -> showOriginGifticonDialog(events.origin)
                    is AddGifticonEvent.ShowExpiredAtDatePicker -> showExpiredAtDatePicker(events.date)
                    is AddGifticonEvent.RequestFocus -> requestFocus(events.focus)
                    is AddGifticonEvent.ShowSnackBar -> showSnackBar(events.uiText)
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

    private fun gotoGallery(list: List<Long>) {
        val intent = Intent(this, GalleryActivity::class.java).apply {
            putExtra(Extras.KEY_SELECTED_IDS, list.toTypedArray())
        }
        gallery.launch(intent)
    }

    private fun gotoCropGifticon(uri: Uri, croppedRect: RectF) {
        val intent = Intent(this, CropGifticonActivity::class.java).apply {
            putExtra(Extras.KEY_ORIGIN_IMAGE, uri)
            putExtra(Extras.KEY_CROPPED_RECT, croppedRect)
        }
        cropGifticon.launch(intent)
    }

    private fun showOriginGifticonDialog(uri: Uri) {
        originImageDialog.apply {
            arguments = Bundle().apply {
                putParcelable(Extras.KEY_ORIGIN_IMAGE, uri)
            }
        }.show(supportFragmentManager, OriginImageDialog::class.java.name)
    }

    private fun showExpiredAtDatePicker(date: Date) {
        expiredAtDatePickerDialog.apply {
            setDate(date)
        }.show(supportFragmentManager, SpinnerDatePicker::class.java.name)
    }

    private fun showConfirmationCancelDialog() {
        confirmationCancelDialog.show(supportFragmentManager, CONFIRMATION_CANCEL_DIALOG)
    }

    private fun showConfirmationDeleteDialog(gifticon: AddGifticonItemUIModel.Gifticon) {
        confirmationDeleteDialog.apply {
            setOnOkClickListener {
                viewModel.deleteGifticon(gifticon)
            }
        }.show(supportFragmentManager, CONFIRMATION_DELETE_DIALOG)
    }

    private fun requestFocus(focus: AddGifticonFocus) {
        val focusView = when (focus) {
            AddGifticonFocus.GIFTICON_NAME -> binding.tietName
            AddGifticonFocus.BRAND_NAME -> binding.tietBrand
            AddGifticonFocus.BARCODE -> binding.tietBarcode
            AddGifticonFocus.BALANCE -> binding.tietBalance
            AddGifticonFocus.MEMO -> binding.tietMemo
            AddGifticonFocus.NONE -> binding.clContainer
        }
        focusView.requestFocus()
        val inputMethodService = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (focus != AddGifticonFocus.NONE) {
            inputMethodService.showSoftInput(focusView, 0)
        } else {
            inputMethodService.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    private fun showSnackBar(uiText: UIText) {
        Snackbar.make(binding.root, uiText.asString(applicationContext), Snackbar.LENGTH_SHORT).show()
    }

    companion object {
        private const val CONFIRMATION_CANCEL_DIALOG = "Tag.ConfirmationCancelDialog"
        private const val CONFIRMATION_DELETE_DIALOG = "Tag.ConfirmationDeleteDialog"

        private const val TEMP_GIFTICON_PREFIX = "temp_gifticon_"
    }
}
