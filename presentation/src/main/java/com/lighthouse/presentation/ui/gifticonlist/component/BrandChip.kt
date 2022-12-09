package com.lighthouse.presentation.ui.gifticonlist.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FilterChip
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode
import com.lighthouse.domain.model.Brand
import com.lighthouse.presentation.R
import com.lighthouse.presentation.ui.common.compose.TextCheckbox
import com.lighthouse.presentation.ui.gifticonlist.GifticonListViewModel
import timber.log.Timber

@Composable
fun BrandChipList(
    modifier: Modifier = Modifier,
    brands: List<Brand> = emptyList(),
    viewModel: GifticonListViewModel = viewModel(),
    selectedFilters: Set<String> = emptySet()
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item { // "전체" 칩
            val entireChipBrand = Brand(
                name = stringResource(id = R.string.main_filter_all),
                count = brands.sumOf { it.count }
            )
            BrandChip(
                brand = entireChipBrand,
                selected = selectedFilters.isEmpty()
            ) {
                viewModel.clearFilter()
            }
        }
        items(brands) { brand ->
            BrandChip(
                brand = brand,
                selected = selectedFilters.contains(brand.name)
            ) {
                viewModel.toggleFilterSelection(brand)
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AllBrandChipsDialog(
    modifier: Modifier = Modifier,
    brands: List<Brand> = emptyList(),
    showExpiredGifticon: Boolean = false,
    selectedFilters: Set<String> = emptySet(),
    viewModel: GifticonListViewModel = viewModel(),
    onDismiss: () -> Unit = {}
) {
    Timber.tag("Compose").d("AllBrandChipsDialog: ${viewModel.viewModelScope.coroutineContext}")
    val interactionSource = remember { MutableInteractionSource() }
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false // 다이얼로그 너비 제한 제거
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize().clickable(
                interactionSource = interactionSource, // Ripple 효과 제거
                indication = null
            ) {
                onDismiss()
            }
        ) {
            Surface(
                modifier = modifier,
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colors.background
            ) {
                Column {
                    TextCheckbox(
                        checked = showExpiredGifticon,
                        textStyle = MaterialTheme.typography.body2,
                        text = stringResource(R.string.gifticon_list_brands_dialog_show_expired_gifticon_option)
                    ) { checked ->
                        viewModel.filterUsedGifticon(checked)
                    }
                    val scrollState = rememberScrollState()
                    FlowRow(
                        modifier = Modifier
                            .padding(16.dp)
                            .verticalScroll(scrollState),
                        mainAxisAlignment = FlowMainAxisAlignment.Start,
                        mainAxisSpacing = 8.dp,
                        mainAxisSize = SizeMode.Expand
                    ) {
                        brands.forEach {
                            BrandChip(
                                brand = it,
                                selected = selectedFilters.contains(it.name)
                            ) { selected ->
                                viewModel.toggleFilterSelection(selected)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BrandChip(
    brand: Brand,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: (Brand) -> Unit = {}
) {
    FilterChip(
        selected = selected,
        onClick = {
            onClick(brand)
        },
        modifier = modifier.wrapContentWidth(),
        colors = ChipDefaults.filterChipColors(
            backgroundColor = MaterialTheme.colors.onSurface.copy(alpha = 0.2f),
            contentColor = MaterialTheme.colors.onSurface,
            selectedBackgroundColor = MaterialTheme.colors.primary,
            selectedContentColor = Color.White
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = brand.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                text = brand.count.toString(),
                modifier = Modifier.padding(start = 4.dp),
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}
