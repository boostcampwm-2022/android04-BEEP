package com.lighthouse.presentation.ui.gifticonlist.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lighthouse.presentation.R
import com.lighthouse.presentation.model.GifticonUIModel
import com.lighthouse.presentation.ui.gifticonlist.GifticonListViewModel

@Composable
fun GifticonListScreen(
    modifier: Modifier = Modifier,
    viewModel: GifticonListViewModel = viewModel(),
) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()
    var removeGifticonDialogState by remember { mutableStateOf<GifticonUIModel?>(null) }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.05f))
            .padding(horizontal = 16.dp),
        color = Color.Transparent,
    ) {
        if (viewState.loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        Column {
            BrandChipListScreen(
                modifier = Modifier.padding(top = 24.dp),
                brands = viewState.brands,
                filters = viewState.selectedFilter,
                onClickEntireBrandDialog = {
                    viewModel.showEntireBrandsDialog()
                },
                onClickTotalChip = {
                    viewModel.clearFilter()
                },
                onClickChip = {
                    viewModel.toggleFilterSelection(it)
                },
            )
            GifticonList(
                gifticons = viewState.gifticons,
                Modifier.padding(top = 8.dp),
                onUse = {
                    viewModel.completeUsage(it)
                },
                onRemove = {
                    removeGifticonDialogState = it
                },
            )
        }
        if (viewState.entireBrandsDialogShown) {
            AllBrandChipsDialog(
                brands = viewState.brands,
                modifier = Modifier
                    .padding(16.dp),
                selectedFilters = viewState.selectedFilter,
                onClickChip = {
                    viewModel.toggleFilterSelection(it)
                },
                onDismiss = {
                    viewModel.dismissEntireBrandsDialog()
                },
            )
        }
    }
    if (removeGifticonDialogState != null) {
        AlertDialog(
            onDismissRequest = { removeGifticonDialogState = null },
            title = {
                Row {
                    Image(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(Color.Red),
                    )
                    Text(stringResource(R.string.gifticon_list_remove_gifticon_dialog_title))
                }
            },
            text = { Text(stringResource(R.string.gifticon_list_remove_gifticon_dialog_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.removeGifticon(
                            gifticon = removeGifticonDialogState ?: return@TextButton,
                        )
                        removeGifticonDialogState = null
                    },
                ) {
                    Text(stringResource(R.string.gifticon_list_remove_gifticon_dialog_remove_button))
                }
            },
        )
    }
}
