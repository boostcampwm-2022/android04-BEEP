package com.lighthouse.presentation.ui.gifticonlist.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FilterChip
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.flowlayout.FlowRow
import com.lighthouse.domain.model.Brand
import com.lighthouse.presentation.R
import com.lighthouse.presentation.ui.gifticonlist.GifticonListViewModel
import timber.log.Timber

@Composable
fun BrandChipList(
    modifier: Modifier = Modifier,
    brands: List<Brand> = emptyList(),
    viewModel: GifticonListViewModel = viewModel(),
    selectedFilters: Set<String> = emptySet()
) {
    Timber.tag("Compose").d("BrandChipList: ${viewModel.viewModelScope.coroutineContext}")
    selectedFilters.forEach {
        Timber.tag("Compose").d("BrandChipList - selected: $it")
    }
    Timber.tag("Compose").d("---END")
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val chipCountToShow = 7

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
        items(brands.subList(0, minOf(chipCountToShow, brands.size))) { brand ->
            BrandChip(
                brand = brand,
                selected = selectedFilters.contains(brand.name)
            ) {
                viewModel.toggleFilterSelection(brand)
            }
        }
    }
}

@Composable
fun AllBrandChipsDialog(
    modifier: Modifier = Modifier,
    brands: List<Brand> = emptyList(),
    selectedFilters: Set<String> = emptySet(),
    viewModel: GifticonListViewModel = viewModel(),
    onDismiss: () -> Unit = {}
) {
    Timber.tag("Compose").d("AllBrandChipsDialog: ${viewModel.viewModelScope.coroutineContext}")

    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties()
    ) {
        Surface(
            modifier = modifier
                .wrapContentHeight(),
            shape = RoundedCornerShape(12.dp),
            color = Color.White
        ) {
            FlowRow(
                modifier = Modifier.padding(16.dp),
                mainAxisSpacing = 10.dp
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
            backgroundColor = colorResource(id = R.color.black).copy(alpha = 0.1f),
            selectedBackgroundColor = colorResource(id = R.color.beep_pink),
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
                color = colorResource(id = R.color.gray_500)
            )
        }
    }
}
