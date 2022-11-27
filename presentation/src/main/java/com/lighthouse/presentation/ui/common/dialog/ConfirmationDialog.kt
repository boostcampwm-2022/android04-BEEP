package com.lighthouse.presentation.ui.common.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.DialogConfirmationBinding
import com.lighthouse.presentation.ui.common.viewBindings

class ConfirmationDialog : DialogFragment(R.layout.dialog_confirmation) {

    private val binding: DialogConfirmationBinding by viewBindings()

    var title: String = ""
    var message: String = ""

    private val defaultOkText by lazy {
        getString(R.string.confirmation_ok)
    }
    var okText: String? = null
    private val defaultCancelText by lazy {
        getString(R.string.confirmation_cancel)
    }
    var cancelText: String? = null

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

            tvTitle.text = title
            tvMessage.text = message
            tvOk.text = okText ?: defaultOkText
            tvCancel.text = cancelText ?: defaultCancelText
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
