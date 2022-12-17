package com.lighthouse.presentation.ui.common.view

import android.content.Context
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText

abstract class FormattedTextInputEditText(context: Context, attrs: AttributeSet) : TextInputEditText(context, attrs) {
    protected var realValue = ""
    protected var displayValue = ""

    fun setValue(newValue: String) {
        if (realValue == newValue) {
            return
        }
        realValue = newValue
        displayValue = valueToTransformed(newValue)
        setText(displayValue, displayValue.length)
    }

    protected fun setText(str: String, selection: Int) {
        setText(str)
        Selection.setSelection(text, Integer.min(selection, text?.length ?: 0))
    }

    init {
        setUpOnTextChanged()
    }

    private fun setUpOnTextChanged() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val newString = s.toString()
                val oldDisplayValue = displayValue
                if (oldDisplayValue == newString) {
                    return
                }
                val newValue = onTransformedNewValue(newString, oldDisplayValue, start, before, count)

                val newDisplayValue = valueToTransformed(newValue)
                val newSelection = calculateSelection(newDisplayValue, oldDisplayValue, start, before, count)

                realValue = newValue
                displayValue = newDisplayValue
                setText(newDisplayValue, newSelection)

                onChangeValueListener?.onChangeValue(newValue)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    protected abstract fun onTransformedNewValue(
        newString: String,
        oldDisplayValue: String,
        start: Int,
        before: Int,
        count: Int
    ): String

    protected abstract fun calculateSelection(
        newDisplayValue: String,
        oldDisplayValue: String,
        start: Int,
        before: Int,
        count: Int
    ): Int

    protected abstract fun valueToTransformed(text: String): String

    protected abstract fun transformedToValue(text: String): String

    var onChangeValueListener: OnChangeValueListener? = null

    interface OnChangeValueListener {
        fun onChangeValue(value: String)
    }
}
