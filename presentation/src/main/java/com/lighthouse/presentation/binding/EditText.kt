package com.lighthouse.presentation.binding

import android.widget.EditText
import androidx.databinding.BindingAdapter
import com.lighthouse.presentation.model.EditTextInfo

@BindingAdapter("setEditTextInfo")
fun setEditTextInfo(editText: EditText, info: EditTextInfo?) {
    info ?: return
    editText.setText(info.text)
    editText.setSelection(info.selection)
}
