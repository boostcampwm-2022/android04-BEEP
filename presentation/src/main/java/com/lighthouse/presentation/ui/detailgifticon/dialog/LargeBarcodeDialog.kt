package com.lighthouse.presentation.ui.detailgifticon.dialog

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.DialogLargeBarcodeBinding
import com.lighthouse.presentation.extension.rotated
import com.lighthouse.presentation.extra.Extras
import com.lighthouse.presentation.ui.common.viewBindings
import com.lighthouse.presentation.util.BarcodeUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LargeBarcodeDialog : DialogFragment(R.layout.dialog_large_barcode) {
    private val binding: DialogLargeBarcodeBinding by viewBindings()
    private val barcode
        get() = arguments?.getString(Extras.KEY_BARCODE) ?: run {
            this.dismiss()
            ""
        }

    @Inject
    lateinit var barcodeUtil: BarcodeUtil

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.root.setOnClickListener {
            dismiss()
        }

        binding.barcode = barcode
        binding.tvBarcodeNumber.text = divideBarcodeNumber(barcode)
        binding.ivBarcode.viewTreeObserver.addOnDrawListener {
            val rotatedBarcode = barcodeUtil.createBarcodeBitmap(
                barcode,
                binding.ivBarcode.height,
                binding.ivBarcode.width
            ).rotated(90f)
            binding.ivBarcode.setImageBitmap(rotatedBarcode)
        }
    }

    override fun onResume() {
        super.onResume()

        dialog?.window?.apply {
            attributes = attributes.apply {
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = WindowManager.LayoutParams.MATCH_PARENT
            }
        }
    }

    private fun divideBarcodeNumber(number: String) = number.chunked(4).joinToString(" ")
}
