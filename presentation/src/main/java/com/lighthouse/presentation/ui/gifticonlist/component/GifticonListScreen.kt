package com.lighthouse.presentation.ui.gifticonlist.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
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
            if (viewState.loading) {
                BrandChipLoadingScreen(modifier = Modifier.padding(top = 24.dp))
            } else {
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
                    }
                )
            }
            if (viewState.loading) {
                GifticonLoadingList()
            } else {
                GifticonList(
                    gifticons = viewState.gifticons,
                    Modifier.padding(top = 8.dp)
                )
            }
        }
        if (viewState.entireBrandsDialogShown) {
            AllBrandChipsDialog(
                brands = viewState.brands,
                modifier = Modifier
                    .padding(16.dp),
                selectedFilters = viewState.selectedFilter,
                showExpiredGifticon = viewState.showExpiredGifticon,
                onCheckFilterExpired = {
                    viewModel.filterUsedGifticon(it)
                },
                onClickChip = {
                    viewModel.toggleFilterSelection(it)
                },
                onDismiss = {
                    viewModel.dismissEntireBrandsDialog()
                }
            )
        }
    }
}
