package com.lighthouse.presentation.ui.common.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle

@Composable
fun TextCheckbox(
    checked: Boolean,
    text: String,
    textStyle: TextStyle = MaterialTheme.typography.body2,
    onCheckedChanged: (checked: Boolean) -> Unit = {}
) {
    var checkedState by remember { mutableStateOf(checked) }
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = checkedState, onCheckedChange = { checked ->
            checkedState = checked
            onCheckedChanged(checkedState)
        })
        Text(
            text = text,
            style = textStyle,
            modifier = Modifier.clickable {
                checkedState = checkedState.not()
                onCheckedChanged(checkedState)
            }
        )
    }
}
