package com.lighthouse.presentation.ui.gifticonlist.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lighthouse.presentation.R
import com.lighthouse.presentation.ui.gifticonlist.GifticonListViewModel

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun GifticonListScreen(
    modifier: Modifier = Modifier,
    viewModel: GifticonListViewModel = viewModel()
) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()

    Surface(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.05f))
            .padding(horizontal = 16.dp),
        color = Color.Transparent
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(top = 24.dp)
            ) {
                BrandChipList(
                    modifier = Modifier.weight(1f),
                    brands = viewState.brands,
                    viewModel = viewModel,
                    selectedFilters = viewState.selectedFilter
                )
                IconButton(
                    modifier = Modifier,
                    onClick = {
                        viewModel.showEntireBrandsDialog()
                    }
                ) {
                    Image(
                        imageVector = Icons.Outlined.Tune,
                        colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
                        contentDescription = stringResource(R.string.gifticon_list_show_all_brand_chips_button)
                    )
                }
            }
            GifticonList(
                gifticons = viewState.gifticons,
                Modifier.padding(top = 8.dp)
            )
        }
        if (viewState.entireBrandsDialogShown) {
            AllBrandChipsDialog(
                brands = viewState.brands,
                modifier = Modifier
                    .padding(16.dp),
                selectedFilters = viewState.selectedFilter,
                showExpiredGifticon = viewState.showExpiredGifticon,
                onDismiss = {
                    viewModel.dismissEntireBrandsDialog()
                }
            )
        }
    }
}
