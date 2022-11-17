package com.lighthouse.presentation.ui.detailgifticon.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.DialogUseGifticonBinding
import com.lighthouse.presentation.extension.repeatOnStarted
import com.lighthouse.presentation.extension.screenHeight
import com.lighthouse.presentation.extension.toConcurrency
import com.lighthouse.presentation.ui.common.viewBindings
import com.lighthouse.presentation.ui.detailgifticon.GifticonDetailViewModel
import com.lighthouse.presentation.util.BarcodeUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@AndroidEntryPoint
class UseGifticonDialog : BottomSheetDialogFragment() {
    private val binding by viewBindings(DialogUseGifticonBinding::bind)
    private val viewModel: GifticonDetailViewModel by activityViewModels()

    @Inject
    lateinit var barcodeUtil: BarcodeUtil

    override fun getTheme(): Int {
        return R.style.Theme_BEEP_BottomSheetDialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.dialog_use_gifticon, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        initBottomSheetDialog(view)

        binding.etAmountToUse.addTextChangedListener {
            val text = it ?: return@addTextChangedListener
            val amountString = text.filter { char -> char.isDigit() }.toString()
            if (amountString.length < 10) {
                val amount = amountString.toIntOrNull() ?: 0
                if (amount <= viewModel.gifticon.value.balance) {
                    viewModel.amountToUse.update { amount }
                }
            }
        }

        repeatOnStarted {
            viewModel.gifticon.collect {
                barcodeUtil.displayBitmap(binding.ivBarcode, it.barcode)
                binding.tvBarcodeNumber.text = divideBarcodeNumber(it.barcode)
            }
        }
        repeatOnStarted {
            viewModel.amountToUse.collect { amount ->
                if (viewModel.gifticon.value.isCashCard) {
                    val displayValue = if (amount == 0) "" else amount.toConcurrency(requireContext(), false)
                    binding.etAmountToUse.setText(displayValue)
                }
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
