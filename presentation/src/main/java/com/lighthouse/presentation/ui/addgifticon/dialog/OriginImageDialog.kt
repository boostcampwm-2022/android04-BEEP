package com.lighthouse.presentation.ui.addgifticon.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.DialogOriginImageBinding
import com.lighthouse.presentation.extension.repeatOnStarted
import com.lighthouse.presentation.ui.common.viewBindings

class OriginImageDialog : DialogFragment(R.layout.dialog_origin_image) {

    private val binding: DialogOriginImageBinding by viewBindings()

    private val viewModel: OriginImageViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            vm = viewModel
        }

        setUpDismissCollect()
    }

    private fun setUpDismissCollect() {
        repeatOnStarted {
            viewModel.dismissFlow.collect {
                dismiss()
            }
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
}
