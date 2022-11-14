package com.lighthouse.presentation.ui.gallery

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.ActivityGalleryBinding
import com.lighthouse.presentation.ui.gallery.adapter.GalleryAdapter
import com.lighthouse.presentation.utils.extention.dp
import com.lighthouse.presentation.utils.recycler.SectionSpaceGridDivider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGalleryBinding

    private val viewModel: GalleryViewModel by viewModels()

    private val galleryAdapter = GalleryAdapter(
        onClickGallery = {}
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_gallery)
        binding.apply {
            lifecycleOwner = this@GalleryActivity
            vm = viewModel
        }

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
            addItemDecoration(SectionSpaceGridDivider(20.dp, 4.dp, 4.dp, 12.dp, 4.dp, 12.dp))
        }
    }
}
