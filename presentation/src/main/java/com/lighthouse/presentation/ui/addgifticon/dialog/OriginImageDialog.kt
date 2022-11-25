package com.lighthouse.presentation.ui.addgifticon.dialog

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.DialogOriginImageBinding
import com.lighthouse.presentation.extension.getParcelableCompat
import com.lighthouse.presentation.extra.Extras
import com.lighthouse.presentation.ui.common.viewBindings
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class OriginImageDialog : DialogFragment(R.layout.dialog_origin_image) {

    private val binding by viewBindings(DialogOriginImageBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
        }

        val uri = arguments?.getParcelableCompat(Extras.OriginImage, Uri::class.java)
        Timber.tag("TEST").d("$uri")

        binding.ivOrigin.setImageURI(uri)

        val drawable = binding.ivOrigin.drawable
        Timber.tag("TEST").d("$drawable")
        Timber.tag("TEST").d("${drawable.minimumWidth} ${drawable.intrinsicWidth}")
        Timber.tag("TEST").d("${drawable.minimumHeight} ${drawable.intrinsicHeight}")

//        binding.root.post{
//            binding.originUri =
//        }
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
