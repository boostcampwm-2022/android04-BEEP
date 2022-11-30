package com.lighthouse.presentation.ui.gifticonlist.component

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FilterChip
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.flowlayout.FlowRow
import com.lighthouse.domain.model.Brand
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.presentation.R
import com.lighthouse.presentation.extra.Extras
import com.lighthouse.presentation.ui.detailgifticon.GifticonDetailActivity
import com.lighthouse.presentation.ui.gifticonlist.GifticonListViewModel
import timber.log.Timber
import java.util.Date

data class GifticonListComponentState(
    val showDialog: Boolean = false
)

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun GifticonListScreen(
    viewModel: GifticonListViewModel = viewModel()
) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()
    val componentState = remember {
        mutableStateOf(
            GifticonListComponentState(
                showDialog = false
            )
        )
    }

    Timber.tag("GifticonList").d("${viewState.brands}")
    Surface(
        modifier = Modifier
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
                BrandChipList(modifier = Modifier.weight(1f), brands = viewState.brands)
                IconButton(
                    modifier = Modifier,
                    onClick = {
                        componentState.value = componentState.value.copy(showDialog = true)
                    }
                ) {
                    Image(
                        Icons.Outlined.Tune,
                        contentDescription = stringResource(R.string.gifticon_list_show_all_brand_chips_button)
                    )
                }
            }
            GifticonList(gifticons = viewState.gifticons, Modifier.padding(top = 64.dp))
        }
        if (componentState.value.showDialog) {
            AllBrandChipsDialog(
                brands = viewState.brands,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onDismiss = { componentState.value = componentState.value.copy(showDialog = false) }
            )
        }
    }
}

@Composable
fun GifticonList(gifticons: List<Gifticon>, modifier: Modifier = Modifier) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier
    ) {
        items(gifticons) { gifticon ->
            GifticonItem(gifticon = gifticon)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GifticonItem(gifticon: Gifticon) {

    val context = LocalContext.current
    val cornerSize = 8.dp

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium.copy(CornerSize(cornerSize)),
        onClick = {
            context.startActivity(
                Intent(context, GifticonDetailActivity::class.java).apply {
                    putExtra(Extras.KEY_GIFTICON_ID, gifticon.id)
                }
            )
        }) {
        Row {
            Image(
                painter = painterResource(id = R.mipmap.ic_launcher),
                contentDescription = "",
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterVertically)
                    .aspectRatio(1f)
            )
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "D-31",
                    modifier = Modifier
                        .clip(RoundedCornerShape(cornerSize))
                        .background(colorResource(id = R.color.beep_pink))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .align(Alignment.End),
                    color = Color.White,
                    style = MaterialTheme.typography.caption
                )
                Text(
                    text = gifticon.name
                )
                Text(
                    text = gifticon.brand
                )
                Text(
                    text = "3,460원",
                    color = colorResource(R.color.beep_pink)
                )
                Text(
                    text = "~ 2022-11-23",
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(bottom = 16.dp, end = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun BrandChipList(
    modifier: Modifier = Modifier,
    brands: List<Brand> = emptyList(),
    onBrandSelected: (HashSet<Brand>) -> Unit = {},
) {
    val selectedBrands = remember { hashSetOf<Brand>() }
    Timber.tag("Compose").d("$brands")
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val chipCountToShow = 3

        item { // "전체" 칩
            val entireChipBrand = Brand(
                name = stringResource(id = R.string.main_filter_all),
                count = brands.sumOf { it.count }
            )
            BrandChip(brand = entireChipBrand, initialSelected = true) {
                selectedBrands.clear()
                onBrandSelected(selectedBrands)
            }
        }
        items(brands.subList(0, minOf(chipCountToShow, brands.size))) { brand ->
            BrandChip(brand) {
                if (selectedBrands.contains(it)) {
                    selectedBrands.remove(it)
                } else {
                    selectedBrands.add(it)
                }
                onBrandSelected(selectedBrands)
            }
        }
    }
}

@Composable
fun AllBrandChipsDialog(
    modifier: Modifier = Modifier,
    brands: List<Brand> = emptyList(),
    onDismiss: () -> Unit = {}
) {
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
                    BrandChip(brand = it)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun BrandChip(
    brand: Brand,
    modifier: Modifier = Modifier,
    initialSelected: Boolean = false,
    onSelect: (Brand) -> Unit = {}
) {
    var selected by remember { mutableStateOf(initialSelected) }
    FilterChip(
        selected = selected,
        onClick = {
            selected = selected.not()
            onSelect(brand)
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

val sampleGifticonItems = listOf(
    Gifticon(
        id = "sample1",
        userId = "mangbaam",
        hasImage = false,
        name = "별다방 아메리카노",
        brand = "스타벅스",
        expireAt = Date(),
        barcode = "808346588450",
        isCashCard = false,
        balance = 0,
        memo = "",
        isUsed = false
    ),
    Gifticon(
        id = "sample2",
        userId = "mangbaam",
        name = "5만원권",
        hasImage = false,
        brand = "GS25",
        expireAt = Date(),
        barcode = "808346588450",
        isCashCard = true,
        balance = 50000,
        memo = "",
        isUsed = false
    ),
    Gifticon(
        id = "sample3",
        userId = "mangbaam",
        name = "어머니는 외계인",
        brand = "베스킨라빈스",
        expireAt = Date(),
        hasImage = false,
        barcode = "808346588450",
        isCashCard = false,
        balance = 0,
        memo = "",
        isUsed = true
    ),
    Gifticon(
        id = "sample4",
        userId = "mangbaam",
        name = "3만원권",
        brand = "e마트",
        expireAt = Date(),
        barcode = "808346588450",
        isCashCard = true,
        balance = 0,
        hasImage = false,
        memo = "",
        isUsed = true
    )
)

@Preview
@Composable
fun ChipPreview() {
    BrandChip(Brand("스타벅스", 10))
}

@Preview
@Composable
fun BrandChipsPreview() {
    BrandChipList(
        brands = listOf(
            Brand("스타벅스", 10),
            Brand("베스킨라빈스", 12),
            Brand("맘스터치", 1),
            Brand("김밥천국", 3),
            Brand("투썸", 7),
        )
    )
}

@Preview(widthDp = 320)
@Composable
fun GifticonItemPreview() {
    GifticonItem(
        sampleGifticonItems[0]
    )
}

@Preview
@Composable
fun GifticonListPreview() {
    GifticonList(
        sampleGifticonItems
    )
}

@Preview
@Composable
fun BrandChipsDialogPreview() {
    AllBrandChipsDialog(
        brands = listOf(
            Brand(name = "스타벅스", count = 18),
            Brand(name = "베스킨라빈스", count = 18),
            Brand(name = "BHC", count = 18),
            Brand(name = "GS25", count = 18),
            Brand(name = "CU", count = 18),
            Brand(name = "서브웨이", count = 18),
            Brand(name = "세븐일레븐", count = 18),
            Brand(name = "파파존스", count = 18)
        )
    )
}
