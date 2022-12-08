package com.lighthouse.presentation.binding

import android.text.Selection
import android.widget.EditText
import androidx.databinding.BindingAdapter
import com.lighthouse.presentation.model.EditTextInfo
import java.lang.Integer.min

@BindingAdapter("setEditTextInfo")
fun setEditTextInfo(editText: EditText, info: EditTextInfo?) {
    info ?: return
    editText.setText(info.text)
    val editable = editText.text
    Selection.setSelection(editable, min(info.selection, editable.length))
}
