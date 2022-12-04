package com.lighthouse.presentation.ui.common.compose

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import timber.log.Timber

@Composable
fun ConcurrencyField(
    modifier: Modifier = Modifier,
    value: Int = 0,
    textStyle: TextStyle? = null,
    enable: Boolean = false,
    suffixText: String = stringResource(id = R.string.all_cash_origin_unit),
    onValueChanged: (Int) -> Unit = {}
) {
    val typography = textStyle ?: MaterialTheme.typography.body1
    var text by remember { mutableStateOf(value.toString()) }

    BasicTextField(
        value = text,
        onValueChange = {
            Timber.tag("Custom").d("TextField text: $it")
            it.filterNot { c -> c == ',' || c == '.' }
                .substring(minOf(10 + suffixText.length, it.length)) // 붙여넣기 했을 때 최대 10개 까지만 입력 되도록 제한
            it.toNumber()
            if (it.isBlank()) {
                onValueChanged(0)
                text = ""
                return@BasicTextField
            }
            if (it.length >= 10) {
                return@BasicTextField
            }
            onValueChanged(it.toNumber().toIntOrNull() ?: 0)
            text = it.trim()
        },
        modifier = modifier,
        readOnly = enable.not(),
        singleLine = true,
        visualTransformation = ConcurrencyFormatVisualTransformation(suffixText),
        textStyle = typography,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
    )
}

class ConcurrencyFormatVisualTransformation(val suffixText: String = "") : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val numberWithComma = text.text.toNumberFormat()

        Timber.tag("Custom").d("mapper text: $text")
        Timber.tag("Custom").d("mapper textWithComma: $numberWithComma")

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
    ConcurrencyField(value = 0, enable = true)
}
