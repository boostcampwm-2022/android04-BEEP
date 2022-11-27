package com.lighthouse.presentation.ui.addgifticon

import android.app.Activity
import android.content.Intent
import android.graphics.RectF
import android.net.Uri
import android.os.Bundle
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
import com.lighthouse.presentation.model.GalleryUIModel
import com.lighthouse.presentation.ui.addgifticon.adapter.AddGifticonAdapter
import com.lighthouse.presentation.ui.addgifticon.dialog.OriginImageDialog
import com.lighthouse.presentation.ui.common.dialog.SpinnerDatePicker
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
            viewModel.deleteGifticon(gifticon)
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

    private val cropGifticon = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val croppedUri =
                result.data?.getParcelable(Extras.KEY_CROPPED_IMAGE, Uri::class.java)
                    ?: return@registerForActivityResult
            val croppedRect = result.data?.getParcelable(Extras.KEY_CROPPED_RECT, RectF::class.java)
                ?: return@registerForActivityResult

            val gifticon = viewModel.selectedGifticon.value ?: return@registerForActivityResult
            val output = getFileStreamPath("Temp${gifticon.id}")

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    FileInputStream(croppedUri.path).copyTo(
                        FileOutputStream(output)
                    )
                }
                viewModel.croppedImage(output.toUri(), croppedRect)
            }
        }
    }

    private val originImageDialog = OriginImageDialog()

    private val expiredAtDatePickerDialog by lazy {
        SpinnerDatePicker(applicationContext) { _, year, month, dayOfMonth ->
            val date = Calendar.getInstance(Locale.getDefault()).let {
                it.set(year, month, dayOfMonth)
                it.time
            }
            viewModel.changeExpiredAt(date)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_gifticon)
        binding.apply {
            lifecycleOwner = this@AddGifticonActivity
            vm = viewModel
        }

        setUpRecyclerView()
        collectEvent()
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
                    is AddGifticonEvents.PopupBackStack -> cancelAddGifticon()
                    is AddGifticonEvents.NavigateToGallery -> gotoGallery(events.list)
                    is AddGifticonEvents.NavigateToCropGifticon -> gotoCropGifticon(events.origin, events.croppedRect)
                    is AddGifticonEvents.ShowOriginGifticon -> showOriginGifticonDialog(events.origin)
                    is AddGifticonEvents.ShowExpiredAtDatePicker -> showExpiredAtDatePicker(events.date)
                    is AddGifticonEvents.RequestFocus -> requestFocus(events.focus)
                    is AddGifticonEvents.ShowSnackBar -> showSnackBar(events.uiText)
                }
            }
        }
    }

    private fun cancelAddGifticon() {
        setResult(Activity.RESULT_CANCELED)
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
        }.show()
    }

    private fun requestFocus(focus: AddGifticonFocus) {
        when (focus) {
            AddGifticonFocus.GIFTICON_NAME -> binding.tietName.requestFocus()
            AddGifticonFocus.BRAND_NAME -> binding.tietBrand.requestFocus()
            AddGifticonFocus.BARCODE -> binding.tietBarcode.requestFocus()
            AddGifticonFocus.EXPIRED_AT -> binding.tietExpireDate.requestFocus()
            AddGifticonFocus.BALANCE -> binding.tietBalance.requestFocus()
            AddGifticonFocus.MEMO -> binding.tietMemo.requestFocus()
            AddGifticonFocus.NONE -> binding.clContainer.requestFocus()
        }
    }

    private fun showSnackBar(uiText: UIText) {
        Snackbar.make(binding.root, uiText.asString(applicationContext), Snackbar.LENGTH_SHORT).show()
    }
}
