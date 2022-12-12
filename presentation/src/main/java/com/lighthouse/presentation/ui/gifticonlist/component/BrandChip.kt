package com.lighthouse.presentation.ui.gifticonlist.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FilterChip
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.lighthouse.domain.model.Brand
import com.lighthouse.presentation.R
import com.lighthouse.presentation.ui.common.compose.TextCheckbox

@Composable
fun BrandChipListScreen(
    modifier: Modifier,
    brands: List<Brand>,
    filters: Set<String>,
    onClickEntireBrandDialog: () -> Unit = {},
    onClickTotalChip: () -> Unit = {},
    onClickChip: (Brand) -> Unit = {}
) {
    Row(
        modifier = modifier
    ) {
        BrandChipList(
            modifier = Modifier.weight(1f),
            brands = brands,
            selectedFilters = filters,
            onClickTotalChip = {
                onClickTotalChip()
            },
            onClickChip = {
                onClickChip(it)
            }
        )
        IconButton(
            modifier = Modifier,
            onClick = {
                onClickEntireBrandDialog()
            }
        ) {
            Image(
                imageVector = Icons.Outlined.Tune,
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
                contentDescription = stringResource(R.string.gifticon_list_show_all_brand_chips_button)
            )
        }
    }
}

@Composable
fun BrandChipList(
    modifier: Modifier = Modifier,
    brands: List<Brand> = emptyList(),
    selectedFilters: Set<String> = emptySet(),
    onClickTotalChip: () -> Unit = {},
    onClickChip: (Brand) -> Unit = {}
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
                onClickTotalChip()
            }
        }
        items(brands) { brand ->
            BrandChip(
                brand = brand,
                selected = selectedFilters.contains(brand.name)
            ) {
                onClickChip(brand)
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
    onCheckFilterExpired: (Boolean) -> Unit = {},
    onClickChip: (Brand) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false // 다이얼로그 너비 제한 제거
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .clickable(
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
                        onCheckFilterExpired(checked)
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
                                onClickChip(selected)
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
            backgroundColor = MaterialTheme.colors.onSurface.copy(alpha = 0.1f),
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

@Composable
fun BrandChipLoadingScreen(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BrandChipLoadingList(
            Modifier
                .weight(1f)
                .padding(end = 16.dp),
            5
        )
        Icon(
            modifier = Modifier.placeholder(
                visible = true,
                highlight = PlaceholderHighlight.shimmer()
            ),
            imageVector = Icons.Outlined.Tune,
            contentDescription = null
        )
    }
}

@Composable
fun BrandChipLoadingList(modifier: Modifier = Modifier, count: Int = 3) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(count) {
            BrandChipLoading()
        }
    }
}

@Composable
fun BrandChipLoading(modifier: Modifier = Modifier) {
    Spacer(
        modifier = modifier
            .size(60.dp, 30.dp)
            .clip(RoundedCornerShape(16.dp))
            .placeholder(
                visible = true,
                highlight = PlaceholderHighlight.shimmer()
            )
    )
}

@Preview(widthDp = 320, heightDp = 700)
@Composable
fun BrandChipLoadingPreview() {
    BrandChipLoading()
}

@Preview
@Composable
fun BrandChipLoadingListPreview() {
    BrandChipLoadingList()
}
