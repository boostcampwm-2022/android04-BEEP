package com.lighthouse.presentation.ui.common

import android.content.Context
import android.content.res.TypedArray
import android.text.InputFilter
import android.util.AttributeSet
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.lighthouse.presentation.R
import timber.log.Timber
import java.text.DecimalFormat

class ConcurrencyEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextInputLayout(context, attrs, defStyleAttr) {

    private val inputEditText: TextInputEditText
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
    private lateinit var unitText: CharSequence // 화폐 구분자. ex) 1,000,000 에서 콤마(,) 를 의미함
    private lateinit var numberFormat: DecimalFormat

    var text = ""
        private set
    var value = 0
        private set

    init {
        val binding = inflate(context, R.layout.custom_concurrency_edit_text, this)
        inputEditText = binding.findViewById(R.id.tiet_value)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ConcurrencyEditText)
        setTypeArray(typedArray)
    }

    private fun setTypeArray(typedArray: TypedArray) {
        val text = typedArray.getText(R.styleable.ConcurrencyEditText_text)
        inputEditText.setText(text)

        val hint = typedArray.getText(R.styleable.ConcurrencyEditText_hint)
        inputEditText.hint = hint

        maxValue = typedArray.getInt(R.styleable.ConcurrencyEditText_maxValue, Int.MAX_VALUE)

        // 화폐 단위를 쪼갤 크기. ex) 1,000,000 일 때 chunkSize 는 3
        chunkSize = typedArray.getInt(R.styleable.ConcurrencyEditText_maxValue, 3)

        // 화폐 구분자. ex) 1,000,000 에서 콤마(,) 를 의미함
        unitText = typedArray.getText(R.styleable.ConcurrencyEditText_unitText) ?: ","

        // 숫자 포맷. ex) 1,000,000 과 같이 나타낼 때 #,### 으로 만듦
        numberFormat = DecimalFormat("#$unitText" + "#".repeat(chunkSize))

        // 밑줄 색상
        val underStrokeColor = typedArray.getColor(R.styleable.ConcurrencyEditText_underStrokeColor, 0)
        boxStrokeColor = underStrokeColor

        typedArray.recycle()
    }

    private fun setTextChangedListener() {
        inputEditText.filters = arrayOf(
            InputFilter { source, start, end, dest, dstart, dend ->
                Timber.tag("concurrency")
                    .d("source: $source, start: $start, end: $end, dest: $dest, dstart: $dstart, dend: $dend")
                val totalString = dest.substring(0 until dstart) + source + dest.substring(dend)
                val number = convertToNumber(totalString)
                if ((dest.toString()
                        .isNotBlank() && number.isBlank()) || (number.isNotBlank() && number.toLong() > maxValue)
                ) {
                    Timber.tag("concurrency").d("source: $source")
                    return@InputFilter ""
                } else {
                    return@InputFilter null
                }
            }
        )
        inputEditText.doOnTextChanged { charSequence, _, _, _ ->
            val newString = charSequence?.toString() ?: ""
            if (text == newString) return@doOnTextChanged

            val number = convertToNumber(newString)
            if (number.isBlank()) return@doOnTextChanged

            text = numberFormat.format(number.toInt())
            inputEditText.setText(text.ifBlank { EMPTY_TEXT })
            inputEditText.setSelection(text.length) // 커서를 맨 뒤로 설정
            Timber.tag("concurrency").d("text: $text")
        }
    }

    private fun convertToNumber(text: String): String {
        val number = text.filter { it.isDigit() }.toIntOrNull()
        return number?.toString() ?: ""
    }

    companion object {
        const val EMPTY_TEXT = "0"
    }
}
