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
import com.lighthouse.core.android.exts.screenHeight
import com.lighthouse.core.android.utils.barcode.BarcodeUtil
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.DialogUseGifticonBinding
import com.lighthouse.presentation.extension.repeatOnStarted
import com.lighthouse.presentation.ui.common.compose.ConcurrencyField
import com.lighthouse.presentation.ui.common.viewBindings
import com.lighthouse.presentation.ui.detailgifticon.GifticonDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
        binding.ctfAmountToBeUsed.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    ConcurrencyField(
                        value = amountToUse.value,
                        textStyle = MaterialTheme.typography.h4.copy(textAlign = TextAlign.End),
                        upperLimit = viewModel.gifticon.value?.balance ?: 0
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
                val rotatedBarcode = withContext(Dispatchers.IO) {
                    barcodeUtil.createBarcodeBitmap(
                        requireContext(),
                        gifticon.barcode,
                        binding.ivBarcode.width,
                        binding.ivBarcode.height
                    )
                }
                binding.ivBarcode.setImageBitmap(rotatedBarcode)
                binding.tvBarcodeNumber.text = divideBarcodeNumber(gifticon.barcode)
            }
        }
        viewLifecycleOwner.repeatOnStarted {
            viewModel.amountToBeUsed.collect {
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
