package com.lighthouse.presentation.ui.gifticonlist.component

import android.content.Intent
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.lighthouse.domain.util.isExpired
import com.lighthouse.presentation.R
import com.lighthouse.presentation.extension.dpToPx
import com.lighthouse.presentation.extension.toConcurrency
import com.lighthouse.presentation.extension.toDday
import com.lighthouse.presentation.extension.toExpireDate
import com.lighthouse.presentation.extra.Extras
import com.lighthouse.presentation.model.GifticonUIModel
import com.lighthouse.presentation.ui.detailgifticon.GifticonDetailActivity
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GifticonList(
    gifticons: List<GifticonUIModel>,
    modifier: Modifier = Modifier,
    onUse: (GifticonUIModel) -> Unit = {},
    onRemove: (GifticonUIModel) -> Unit = {},
) {
    var showExpiredGifticons by rememberSaveable { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 36.dp),
    ) {
        itemsIndexed(items = gifticons, key = { _, item -> item.id }) { index, gifticon ->
            var space = 8.dp

            // 사용 기한 만료된 기프티콘 토글 열
            if (
                index > 0 && gifticons.lastIndex >= index &&
                gifticons[index - 1].expireAt.isExpired().not() && gifticon.expireAt.isExpired()
            ) {
                ExpiredGifticonToggleColumn(showList = showExpiredGifticons) {
                    showExpiredGifticons = it
                }
                space = 0.dp
            }

            // 기프티콘 목록
            if (gifticon.expireAt.isExpired().not() || showExpiredGifticons) {
                if (index > 0) {
                    Spacer(Modifier.height(space))
                }
                GifticonItem(
                    gifticon = gifticon,
                    modifier = Modifier.animateItemPlacement(
                        animationSpec = tween(600),
                    ),
                    onUse = { onUse(it) },
                    onRemove = { onRemove(it) },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GifticonItem(
    gifticon: GifticonUIModel,
    modifier: Modifier = Modifier,
    onUse: (GifticonUIModel) -> Unit = {},
    onRemove: (GifticonUIModel) -> Unit = {},
) {
    val context = LocalContext.current
    val cornerSize = 8.dp

    val swipeableState = rememberSwipeableState(initialValue = 0)
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(cornerSize))
            .swipeable(
                state = swipeableState,
                anchors = mapOf(
                    0f to 0,
                    -(100f.dpToPx) to 1,
                    (100f.dpToPx) to 2,
                ),
                thresholds = { _, _ ->
                    FractionalThreshold(0.3f)
                },
                orientation = Orientation.Horizontal,
            )
            .background(if (swipeableState.offset.value < 0) colorResource(id = R.color.point_green_dark) else Color.Red),
    ) {
        TextButton(
            onClick = {
                onRemove(gifticon)
                scope.launch {
                    swipeableState.animateTo(0, tween(600, 0))
                }
            },
            modifier = Modifier.align(Alignment.CenterStart).padding(start = 20.dp).wrapContentWidth(),
        ) {
            Text(
                text = stringResource(id = R.string.all_remove),
                style = MaterialTheme.typography.button.copy(fontSize = 16.sp),
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
            )
        }
        TextButton(
            onClick = {
                onUse(gifticon)
                scope.launch {
                    swipeableState.animateTo(0, tween(300, 0))
                }
            },
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 20.dp).wrapContentWidth(),
        ) {
            Text(
                text = stringResource(id = R.string.gifticon_list_use_complete),
                style = MaterialTheme.typography.button.copy(fontSize = 16.sp),
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
            )
        }

        Card(
            modifier = Modifier
                .offset {
                    IntOffset(swipeableState.offset.value.roundToInt(), 0)
                }
                .fillMaxWidth()
                .height(130.dp),
            shape = MaterialTheme.shapes.medium.copy(CornerSize(cornerSize)),
            onClick = {
                context.startActivity(
                    Intent(context, GifticonDetailActivity::class.java).apply {
                        putExtra(Extras.KEY_GIFTICON_ID, gifticon.id)
                    },
                )
            },
        ) {
            Row {
                GlideImage(
                    imageModel = { gifticon.croppedUri },
                    imageOptions = ImageOptions(
                        contentScale = ContentScale.Crop,
                        contentDescription = stringResource(R.string.gifticon_product_image),
                        alignment = Alignment.Center,
                    ),
                    modifier = Modifier
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(topStart = cornerSize, bottomStart = cornerSize))
                        .aspectRatio(1f)
                        .align(Alignment.CenterVertically),
                )
                Box(
                    modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.surface),
                ) {
                    Text(
                        text = gifticon.expireAt.toDday(context),
                        modifier = Modifier
                            .clip(RoundedCornerShape(cornerSize))
                            .background(
                                if (gifticon.expireAt.isExpired()) {
                                    Color.Gray
                                } else {
                                    MaterialTheme.colors.primary
                                },
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .align(Alignment.TopEnd),
                        color = Color.White,
                        style = MaterialTheme.typography.caption,
                    )
                    Text(
                        text = gifticon.expireAt.toExpireDate(context).toString(),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(bottom = 16.dp, end = 16.dp),
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = gifticon.brand,
                            style = MaterialTheme.typography.body2,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                        )
                        Text(
                            text = gifticon.name,
                            maxLines = 1,
                            style = MaterialTheme.typography.body1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        if (gifticon.isCashCard) {
                            Text(
                                modifier = Modifier.padding(top = 4.dp),
                                text = gifticon.balance.toConcurrency(context, true),
                                color = colorResource(R.color.beep_pink),
                            )
                        } else {
                            Spacer(Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExpiredGifticonToggleColumn(showList: Boolean, modifier: Modifier = Modifier, onClick: (Boolean) -> Unit) {
    val icon = if (showList) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(showList.not()) }
            .padding(vertical = 12.dp, horizontal = 8.dp),
    ) {
        Image(imageVector = icon, contentDescription = "show or hide expired gifticons icon")
        Text(text = "사용 기한이 만료된 기프티콘", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
    }
}

@Composable
fun GifticonLoadingList(count: Int = 5) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 36.dp),
    ) {
        items(count) {
            GifticonLoadingItem()
        }
    }
}

@Composable
fun GifticonLoadingItem() {
    val cornerSize = 8.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp),
        shape = MaterialTheme.shapes.medium.copy(CornerSize(cornerSize)),
    ) {
        Row {
            Spacer(
                modifier = Modifier.fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = cornerSize, bottomStart = cornerSize))
                    .aspectRatio(1f)
                    .align(Alignment.CenterVertically)
                    .placeholder(
                        visible = true,
                        highlight = PlaceholderHighlight.shimmer(),
                    ),
            )
            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                Text(
                    text = "D-00",
                    modifier = Modifier
                        .clip(RoundedCornerShape(cornerSize))
                        .placeholder(
                            visible = true,
                            highlight = PlaceholderHighlight.shimmer(),
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .align(Alignment.TopEnd),
                )
                Text(
                    text = "~ 2022.00.00",
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 16.dp, end = 16.dp)
                        .placeholder(
                            visible = true,
                            highlight = PlaceholderHighlight.shimmer(),
                        ),
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        modifier = Modifier
                            .padding(bottom = 4.dp)
                            .placeholder(
                                visible = true,
                                highlight = PlaceholderHighlight.shimmer(),
                            ),
                        text = "브랜드 자리",
                    )
                    Text(
                        modifier = Modifier
                            .placeholder(
                                visible = true,
                                highlight = PlaceholderHighlight.shimmer(),
                            ),
                        text = "제목이 들어갈 자리입니다",
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ExpiredGifticonToggleColumnPreview() {
    ExpiredGifticonToggleColumn(true) {}
}

@Preview
@Composable
fun GifticonLoadingPreview() {
    GifticonLoadingItem()
}

@Preview
@Composable
fun GifticonListLoadingPreview() {
    GifticonLoadingList(3)
}
