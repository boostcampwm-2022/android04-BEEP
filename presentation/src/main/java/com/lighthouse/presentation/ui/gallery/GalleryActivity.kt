package com.lighthouse.presentation.ui.gallery

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.ActivityGalleryBinding
import com.lighthouse.presentation.extension.dp
import com.lighthouse.presentation.extension.getParcelableArrayList
import com.lighthouse.presentation.extension.repeatOnStarted
import com.lighthouse.presentation.extra.Extras
import com.lighthouse.presentation.model.GalleryUIModel
import com.lighthouse.presentation.ui.gallery.adapter.GalleryAdapter
import com.lighthouse.presentation.ui.gallery.adapter.GallerySelection
import com.lighthouse.presentation.ui.gallery.event.GalleryEvents
import com.lighthouse.presentation.util.recycler.GridSectionSpaceItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGalleryBinding

    private val viewModel: GalleryViewModel by viewModels()

    private val selection by lazy {
        val list =
            intent.getParcelableArrayList(Extras.GallerySelection, GalleryUIModel.Gallery::class.java) ?: emptyList()
        GallerySelection(list)
    }

    private val galleryAdapter by lazy {
        GalleryAdapter(selection, onClickGallery = {
            viewModel.selectItem(selection.size)
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_gallery)
        binding.apply {
            lifecycleOwner = this@GalleryActivity
            vm = viewModel
        }

        setUpOnBackPressed()
        setUpRecyclerView()
        collectPagingData()
        collectEvent()
    }

    private fun setUpOnBackPressed() {
        onBackPressedDispatcher.addCallback {
            cancelPhotoSelection()
        }
    }

    private fun setUpRecyclerView() {
        val spanCount = 3
        binding.rvList.apply {
            adapter = galleryAdapter
            layoutManager = GridLayoutManager(context, spanCount).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (galleryAdapter.getItemViewType(position) == GalleryAdapter.TYPE_HEADER) spanCount else 1
                    }
                }
            }
            addItemDecoration(GridSectionSpaceItemDecoration(20.dp, 4.dp, 4.dp, 12.dp, 4.dp, 12.dp))
        }
    }

    private fun collectPagingData() {
        repeatOnStarted {
            viewModel.list.collect {
                galleryAdapter.submitData(it)
            }
        }
    }

    private fun collectEvent() {
        repeatOnStarted {
            viewModel.eventsFlow.collect { events ->
                when (events) {
                    GalleryEvents.COMPLETE -> completePhotoSelection()
                    GalleryEvents.CANCEL -> cancelPhotoSelection()
                }
            }
        }
    }

    private fun completePhotoSelection() {
        val intent = Intent().apply {
            putParcelableArrayListExtra(Extras.GallerySelection, selection.toArrayList())
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun cancelPhotoSelection() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}
