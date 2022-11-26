package com.lighthouse.presentation.ui.common

import android.content.Context
import android.content.res.TypedArray
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.google.android.material.textfield.TextInputLayout
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.CustomConcurrencyEditTextBinding

class ConcurrencyEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextInputLayout(context, attrs, defStyleAttr) {

    private val binding = DataBindingUtil.inflate<CustomConcurrencyEditTextBinding>(
        LayoutInflater.from(context),
        R.layout.custom_concurrency_edit_text,
        this,
        true
    )
    private val container by lazy { binding.tilContainer }
    private val inputEditText by lazy { binding.tietValue }

    var maxValue: Int = Int.MAX_VALUE
        set(value) {
            setTextChangedListener()
            field = value
        }
    var chunkSize: Int = 3
        set(value) {
            setTextChangedListener()
            field = value
        }

    init {
        initAttrs()

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ConcurrencyEditText)
        setTypeArray(typedArray)
    }

    private fun initAttrs() {
        with(container) {
            suffixText = context.getString(R.string.all_cash_origin_unit)
            setSuffixTextAppearance(R.style.BEEP_TextStyle_H5)
            isHelperTextEnabled = false
            isHintEnabled = false
            boxBackgroundColor = context.getColor(android.R.color.transparent)
        }
        with(inputEditText) {
            inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
            setTextAppearance(R.style.BEEP_TextStyle_H5)
        }
    }

    private fun setTypeArray(typedArray: TypedArray) {
        val text = typedArray.getText(R.styleable.ConcurrencyEditText_text)
        inputEditText.setText(text)

        val hint = typedArray.getText(R.styleable.ConcurrencyEditText_hint)
        inputEditText.hint = hint

        maxValue = typedArray.getInt(R.styleable.ConcurrencyEditText_maxValue, Int.MAX_VALUE)

        chunkSize = typedArray.getInt(R.styleable.ConcurrencyEditText_maxValue, 3)

        val unitText = typedArray.getText(R.styleable.ConcurrencyEditText_unitText)
        container.suffixText = unitText

        val textColor = typedArray.getColor(R.styleable.ConcurrencyEditText_textColor, 0)
        inputEditText.setTextColor(textColor)

        val underStrokeColor = typedArray.getColor(R.styleable.ConcurrencyEditText_underStrokeColor, 0)
        container.boxStrokeColor = underStrokeColor

        typedArray.recycle()
    }

    private fun setTextChangedListener() {
        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                /*val number = convertToNumber(s?.toString() ?: return)
                if (number.isNotBlank()) {
                    inputEditText.setText(number.chunked(chunkSize).joinToString(","))
                } else {
                    inputEditText.setText("")
                }*/
            }
        })
    }

    private fun convertToNumber(text: String): String {
        val number = text.filter { it.isDigit() }.toIntOrNull()
        return number?.toString() ?: ""
    }
}
