package com.lighthouse.presentation.ui.detailgifticon.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.DialogUseGifticonBinding
import com.lighthouse.presentation.extension.repeatOnStarted
import com.lighthouse.presentation.extension.screenHeight
import com.lighthouse.presentation.extension.screenWidth
import com.lighthouse.presentation.ui.detailgifticon.GifticonDetailViewModel
import com.lighthouse.presentation.util.BarcodeUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UseGifticonDialog : BottomSheetDialogFragment() {
    private lateinit var binding: DialogUseGifticonBinding
    private val viewModel: GifticonDetailViewModel by activityViewModels()

    @Inject lateinit var barcodeUtil: BarcodeUtil

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_use_gifticon, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        binding.root.minimumHeight = (screenHeight * 0.9).toInt()
        binding.root.minimumWidth = (screenWidth * 0.9).toInt()

        repeatOnStarted {
            viewModel.gifticon.collect {
                barcodeUtil.displayBitmap(binding.ivBarcode, it.barcode)
                binding.tvBarcodeNumber.text = it.barcode
            }
        }
    }

    companion object {
        const val TAG: String = "UseGifticonDialog"
    }
}
