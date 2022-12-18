package com.lighthouse.presentation.ui.gifticonlist.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lighthouse.presentation.R
import com.lighthouse.presentation.model.GifticonSortBy
import com.lighthouse.presentation.ui.gifticonlist.GifticonListViewModel

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun GifticonAppBar(
    viewModel: GifticonListViewModel = viewModel()
) {
    val viewState = viewModel.state.collectAsStateWithLifecycle()
    val sortBy: GifticonSortBy = viewState.value.sortBy

    var expanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = {},
        backgroundColor = if (MaterialTheme.colors.isLight) {
            MaterialTheme.colors.primary
        } else {
            MaterialTheme.colors.onSurface.copy(0.09f)
        },
        actions = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .clickable {
                            expanded = true
                        }.align(Alignment.Center),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(id = sortBy.stringRes),
                        color = Color.White,
                        style = MaterialTheme.typography.h6
                    )
                    Icon(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = stringResource(R.string.gifticon_list_toolbar_dropdown_icon_description),
                        tint = Color.White
                    )
                }
            }

            DropDownToolbarMenu(
                expanded = expanded,
                onDismiss = { expanded = false }
            )
        }
    )
}

@Composable
fun DropDownToolbarMenu(
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
    viewModel: GifticonListViewModel = viewModel(),
    onDismiss: () -> Unit = {}
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onDismiss() },
        modifier = modifier.fillMaxWidth()
    ) {
        GifticonSortBy.values().forEach {
            DropdownMenuItem(
                onClick = {
                    viewModel.sort(it)
                    onDismiss()
                }
            ) {
                Text(
                    text = stringResource(id = it.stringRes),
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colors.onSurface,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
