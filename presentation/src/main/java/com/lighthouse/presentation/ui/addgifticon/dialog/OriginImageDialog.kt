package com.lighthouse.presentation.ui.addgifticon.dialog

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.DialogOriginImageBinding
import com.lighthouse.presentation.ui.common.viewBindings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OriginImageDialog : DialogFragment(R.layout.dialog_origin_image) {

    private val binding: DialogOriginImageBinding by viewBindings()

    private val viewModel: OriginImageViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            vm = viewModel
        }
    }

    override fun onResume() {
        super.onResume()

        val window = dialog?.window
        window?.attributes = window?.attributes?.apply {
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.MATCH_PARENT
        }
    }
}
