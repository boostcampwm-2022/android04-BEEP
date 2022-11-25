package com.lighthouse.presentation.ui.addgifticon

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
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
import com.lighthouse.presentation.ui.addgifticon.event.AddGifticonDirections
import com.lighthouse.presentation.ui.cropgifticon.CropGifticonActivity
import com.lighthouse.presentation.ui.gallery.GalleryActivity
import com.lighthouse.presentation.util.recycler.ListSpaceItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileInputStream
import java.io.FileOutputStream

@AndroidEntryPoint
class AddGifticonActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddGifticonBinding

    private val viewModel: AddGifticonViewModel by viewModels()

    private val gifticonAdapter = AddGifticonAdapter(
        onClickGallery = {
            viewModel.gotoGallery()
        },
        onClickGifticon = { position ->
            viewModel.selectGifticon(position)
        },
        onDeleteGifticon = { position ->
            viewModel.deleteGifticon(position)
        }
    )

    private val gallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val list = result.data?.getParcelableArrayList(Extras.GallerySelection, GalleryUIModel.Gallery::class.java)
                ?: emptyList()
            viewModel.loadGalleryImages(list)
        }
    }

    private val cropGifticon = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val croppedUri =
                result.data?.getParcelable(Extras.CroppedImage, Uri::class.java) ?: return@registerForActivityResult
            val gifticon = viewModel.currentGifticon.value ?: return@registerForActivityResult
            val output = getFileStreamPath("Temp${gifticon.id}")

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    FileInputStream(croppedUri.path).copyTo(
                        FileOutputStream(output)
                    )
                }
                viewModel.croppedImage(output.toUri())
            }
        }
    }

    private val originImageDialog = OriginImageDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_gifticon)
        binding.apply {
            lifecycleOwner = this@AddGifticonActivity
            vm = viewModel
        }

        setUpRecyclerView()
        setUpDirections()
    }

    private fun setUpRecyclerView() {
        binding.rvGifticon.apply {
            adapter = gifticonAdapter
            addItemDecoration(ListSpaceItemDecoration(4.dp, 16.dp, 0f, 16.dp, 0f))
        }

        repeatOnStarted {
            viewModel.displayList.collect { list ->
                gifticonAdapter.submitList(list) {
                    binding.rvGifticon.invalidateItemDecorations()
                }
            }
        }
    }

    private fun setUpDirections() {
        repeatOnStarted {
            viewModel.directionsFlow.collect { directions ->
                navigate(directions)
            }
        }
    }

    private fun navigate(directions: AddGifticonDirections) {
        when (directions) {
            AddGifticonDirections.Back -> {
                cancelAddGifticon()
            }
            is AddGifticonDirections.Gallery -> {
                gotoGallery(directions.list)
            }
            is AddGifticonDirections.CropGifticon -> {
                gotoCropGifticon(directions.origin)
            }
            is AddGifticonDirections.OriginGifticon -> {
                showOriginGifticonDialog(directions.origin)
            }
        }
    }

    private fun cancelAddGifticon() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun gotoGallery(list: List<GalleryUIModel.Gallery>) {
        val intent = Intent(this, GalleryActivity::class.java).apply {
            putParcelableArrayListExtra(Extras.GallerySelection, ArrayList(list))
        }
        gallery.launch(intent)
    }

    private fun gotoCropGifticon(uri: Uri) {
        val intent = Intent(this, CropGifticonActivity::class.java).apply {
            putExtra(Extras.OriginImage, uri)
        }
        cropGifticon.launch(intent)
    }

    private fun showOriginGifticonDialog(uri: Uri) {
        originImageDialog.apply {
            arguments = Bundle().apply {
                putParcelable(Extras.OriginImage, uri)
            }
            show(supportFragmentManager, OriginImageDialog::class.java.name)
        }
    }
}
