package com.lighthouse.presentation.ui.common.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.lighthouse.presentation.R
import com.lighthouse.presentation.extension.toNumber
import com.lighthouse.presentation.extension.toNumberFormat
import timber.log.Timber

@Composable
fun ConcurrencyField(
    modifier: Modifier = Modifier,
    value: Int,
    textStyle: TextStyle? = null,
    enable: Boolean = false,
    onValueChanged: (Int) -> Unit = {}
) {
    val typography = textStyle ?: MaterialTheme.typography.body1
    var text by remember { mutableStateOf(value.toString()) }
    Row {
        BasicTextField(
            value = text,
            onValueChange = {
                it.toNumber()
                if (it.isBlank()) {
                    onValueChanged(0)
                    text = ""
                    return@BasicTextField
                }
                if (it.length >= 10) {
                    return@BasicTextField
                }
                it.filterNot { c -> c == ',' || c == '.' }
                    .substring(minOf(10, it.length)) // 붙여넣기 했을 때 최대 10개 까지만 입력 되도록 제한
                onValueChanged(it.toNumber().toIntOrNull() ?: 0)
                text = it
            },
            readOnly = enable.not(),
            modifier = modifier
                .weight(1f)
                .background(Color.Transparent),
            singleLine = true,
            visualTransformation = ConcurrencyFormatVisualTransformation(),
            textStyle = typography.copy(textAlign = TextAlign.End),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Text(
            text = stringResource(id = R.string.all_cash_origin_unit),
            modifier = modifier.align(Alignment.CenterVertically),
            style = typography
        )
    }
}

class ConcurrencyFormatVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val numberWithComma = text.text.toNumberFormat()
        val commas = numberWithComma.count { it == ',' }

        return TransformedText(
            text = AnnotatedString(if (text.isEmpty()) "" else numberWithComma),
            offsetMapping = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    return offset + commas
                }

                override fun transformedToOriginal(offset: Int): Int {
                    return when (offset) {
                        8, 7 -> offset - 2
                        6 -> if (commas == 1) 5 else 4
                        5 -> if (commas == 1) 4 else if (commas == 2) 3 else offset
                        4, 3 -> if (commas == 2) offset - 1 else offset
                        2 -> if (commas == 2) 1 else offset
                        else -> offset
                    }
                }
            }
        )
    }
}

@Preview
@Composable
fun ConcurrencyFieldPreview() {
    ConcurrencyField(value = 10000, enable = true) {
        Timber.tag("Custom").d("금액 변경: $it")
    }
}
