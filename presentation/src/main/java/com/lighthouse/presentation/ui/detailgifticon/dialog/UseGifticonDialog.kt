package com.lighthouse.presentation.ui.detailgifticon.dialog

import android.os.Bundle
import android.view.View
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.style.TextAlign
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.DialogUseGifticonBinding
import com.lighthouse.presentation.extension.repeatOnStarted
import com.lighthouse.presentation.extension.screenHeight
import com.lighthouse.presentation.ui.common.compose.ConcurrencyField
import com.lighthouse.presentation.ui.common.viewBindings
import com.lighthouse.presentation.ui.detailgifticon.GifticonDetailViewModel
import com.lighthouse.presentation.util.BarcodeUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UseGifticonDialog : BottomSheetDialogFragment(R.layout.dialog_use_gifticon) {
    private val binding: DialogUseGifticonBinding by viewBindings()
    private val viewModel: GifticonDetailViewModel by activityViewModels()
    private var amountToUse = mutableStateOf(0)

    @Inject
    lateinit var barcodeUtil: BarcodeUtil

    override fun getTheme(): Int {
        return R.style.Theme_BEEP_BottomSheetDialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.ctfAmountToUse.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    ConcurrencyField(
                        value = amountToUse.value,
                        textStyle = MaterialTheme.typography.h4.copy(textAlign = TextAlign.End)
                    ) {
                        amountToUse.value = it
                    }
                }
            }
        }

        initBottomSheetDialog(view)

        viewLifecycleOwner.repeatOnStarted {
            viewModel.gifticon.collect {
                val gifticon = it ?: return@collect
                barcodeUtil.displayBitmap(binding.ivBarcode, gifticon.barcode)
                binding.tvBarcodeNumber.text = divideBarcodeNumber(gifticon.barcode)
            }
        }
        viewLifecycleOwner.repeatOnStarted {
            viewModel.amountToUse.collect {
                amountToUse.value = it
            }
        }
    }

    private fun initBottomSheetDialog(view: View) {
        val bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        binding.layoutContainer.minHeight = (screenHeight * 0.9).toInt()
    }

    private fun divideBarcodeNumber(number: String) = number.chunked(4).joinToString(" ")

    companion object {
        const val TAG: String = "UseGifticonDialog"
    }
}
