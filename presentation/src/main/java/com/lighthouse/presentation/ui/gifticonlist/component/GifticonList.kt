package com.lighthouse.presentation.ui.gifticonlist.component

import android.content.Intent
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.domain.util.isExpired
import com.lighthouse.presentation.R
import com.lighthouse.presentation.extension.toConcurrency
import com.lighthouse.presentation.extension.toDday
import com.lighthouse.presentation.extension.toExpireDate
import com.lighthouse.presentation.extra.Extras
import com.lighthouse.presentation.ui.detailgifticon.GifticonDetailActivity
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun GifticonList(gifticons: List<Gifticon>, modifier: Modifier = Modifier) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 36.dp)
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
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp),
        shape = MaterialTheme.shapes.medium.copy(CornerSize(cornerSize)),
        onClick = {
            context.startActivity(
                Intent(context, GifticonDetailActivity::class.java).apply {
                    putExtra(Extras.KEY_GIFTICON_ID, gifticon.id)
                }
            )
        }
    ) {
        Row {
            GlideImage(
                imageModel = { context.getFileStreamPath(gifticon.croppedPath) },
                imageOptions = ImageOptions(
                    contentScale = ContentScale.Crop,
                    contentDescription = stringResource(R.string.gifticon_product_image),
                    alignment = Alignment.Center
                ),
                modifier = Modifier
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = cornerSize, bottomStart = cornerSize))
                    .aspectRatio(1f)
                    .align(Alignment.CenterVertically)
            )
            Box(
                modifier = Modifier.fillMaxSize()
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
                            }
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .align(Alignment.TopEnd),
                    color = Color.White,
                    style = MaterialTheme.typography.caption
                )
                Text(
                    text = gifticon.expireAt.toExpireDate(context),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 16.dp, end = 16.dp),
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = gifticon.brand,
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = gifticon.name,
                        maxLines = 1,
                        style = MaterialTheme.typography.body1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (gifticon.isCashCard) {
                        Text(
                            modifier = Modifier.padding(top = 4.dp),
                            text = gifticon.balance.toConcurrency(context, true),
                            color = colorResource(R.color.beep_pink)
                        )
                    } else {
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun GifticonLoadingList() {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 36.dp)
    ) {
        items(5) {
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
        shape = MaterialTheme.shapes.medium.copy(CornerSize(cornerSize))
    ) {
        Row {
            Spacer(
                modifier = Modifier.fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = cornerSize, bottomStart = cornerSize))
                    .aspectRatio(1f)
                    .align(Alignment.CenterVertically)
                    .placeholder(
                        visible = true,
                        highlight = PlaceholderHighlight.shimmer()
                    )
            )
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "D-00",
                    modifier = Modifier
                        .clip(RoundedCornerShape(cornerSize))
                        .placeholder(
                            visible = true,
                            highlight = PlaceholderHighlight.shimmer()
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .align(Alignment.TopEnd)
                )
                Text(
                    text = "~ 2022.00.00",
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 16.dp, end = 16.dp)
                        .placeholder(
                            visible = true,
                            highlight = PlaceholderHighlight.shimmer()
                        )
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        modifier = Modifier
                            .padding(bottom = 4.dp)
                            .placeholder(
                                visible = true,
                                highlight = PlaceholderHighlight.shimmer()
                            ),
                        text = "브랜드 자리"
                    )
                    Text(
                        modifier = Modifier
                            .placeholder(
                                visible = true,
                                highlight = PlaceholderHighlight.shimmer()
                            ),
                        text = "제목이 들어갈 자리입니다"
                    )
                }
            }
        }
    }
}
