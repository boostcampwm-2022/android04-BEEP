package com.lighthouse.presentation.ui.common.compose

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.lighthouse.presentation.R
import com.lighthouse.presentation.extension.toNumber
import com.lighthouse.presentation.extension.toNumberFormat

@Composable
fun ConcurrencyField(
    modifier: Modifier = Modifier,
    value: Int = 0,
    textStyle: TextStyle? = null,
    editable: Boolean = true,
    suffixText: String = stringResource(id = R.string.all_cash_origin_unit),
    onValueChanged: (Int) -> Unit = {}
) {
    val typography = textStyle ?: MaterialTheme.typography.body1
    var text = value.toString()

    BasicTextField(
        value = text,
        onValueChange = {
            val inputText = it.filterNot { c -> c == ',' || c == '.' }.let { filtered ->
                filtered.substring(0, minOf(10, filtered.length)) // 붙여넣기 했을 때 최대 10개 까지만 입력 되도록 제한
            }.toNumber()
            if (inputText.isBlank()) {
                onValueChanged(0)
                text = ""
                return@BasicTextField
            }
            if (inputText.length >= 10) {
                return@BasicTextField
            }
            onValueChanged(inputText.toNumber().toIntOrNull() ?: 0)
            text = inputText
        },
        modifier = modifier,
        readOnly = editable.not(),
        singleLine = true,
        visualTransformation = ConcurrencyFormatVisualTransformation(suffixText),
        textStyle = typography,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
    )
}

class ConcurrencyFormatVisualTransformation(val suffixText: String = "") : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val numberWithComma = text.text.toNumberFormat()

        return TransformedText(
            text = AnnotatedString(numberWithComma + suffixText),
            offsetMapping = object : OffsetMapping {

                override fun originalToTransformed(offset: Int): Int {
                    val rightLength = text.lastIndex - offset
                    val commasAtRight = rightLength / CHUNK_SIZE
                    return numberWithComma.lastIndex - rightLength - commasAtRight
                }

                override fun transformedToOriginal(offset: Int): Int {
                    val commas = (text.lastIndex / CHUNK_SIZE).coerceAtLeast(0)
                    val rightOffset = numberWithComma.length - offset
                    val commasAtRight = rightOffset / (CHUNK_SIZE + 1)
                    return if (offset >= (numberWithComma + suffixText).length) {
                        text.length
                    } else {
                        offset - (commas - commasAtRight)
                    }
                }
            }
        )
    }

    companion object {
        const val CHUNK_SIZE = 3
    }
}

@Preview
@Composable
fun ConcurrencyFieldPreview() {
    ConcurrencyField(value = 0, editable = true)
}
