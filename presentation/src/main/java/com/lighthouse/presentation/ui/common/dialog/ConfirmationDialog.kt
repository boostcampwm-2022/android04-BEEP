package com.lighthouse.presentation.ui.common.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.DialogConfirmationBinding
import com.lighthouse.presentation.ui.common.viewBindings

class ConfirmationDialog : DialogFragment(R.layout.dialog_confirmation) {

    private val binding: DialogConfirmationBinding by viewBindings()

    private var initTitle: String? = null
    private var initMessage: String? = null
    private var initOkText: String? = null
    private var initCancelText: String? = null

    fun setTitle(title: String) {
        initTitle = title
    }

    fun setMessage(message: String) {
        initMessage = message
    }

    fun setOkText(okText: String) {
        initOkText = okText
    }

    fun setCancelText(cancelText: String) {
        initCancelText = cancelText
    }

    private var onOkClickListener: OnClickListener? = null
    fun setOnOkClickListener(listener: (() -> Unit)?) {
        onOkClickListener = listener?.let {
            OnClickListener { it() }
        }
    }

    private var onCancelClickListener: OnClickListener? = null
    fun setOnCancelListener(listener: (() -> Unit)?) {
        onCancelClickListener = listener?.let {
            OnClickListener { it() }
        }
    }

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
        }

        binding.tvTitle.apply {
            text = initTitle ?: text
            isVisible = text != ""
        }
        binding.tvMessage.apply {
            text = initMessage ?: text
            isVisible = text != ""
        }
        binding.tvOk.apply {
            text = initOkText ?: text
            isVisible = text != ""
        }
        binding.tvCancel.apply {
            text = initCancelText ?: text
            isVisible = text != ""
        }

        setUpClickListener()
    }

    private fun setUpClickListener() {
        binding.root.setOnClickListener {
            dismiss()
        }

        binding.tvOk.setOnClickListener { v ->
            if (onOkClickListener != null) {
                onOkClickListener?.onClick(v)
            }
            dismiss()
        }
        binding.tvCancel.setOnClickListener { v ->
            if (onCancelClickListener != null) {
                onCancelClickListener?.onClick(v)
            }
            dismiss()
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
