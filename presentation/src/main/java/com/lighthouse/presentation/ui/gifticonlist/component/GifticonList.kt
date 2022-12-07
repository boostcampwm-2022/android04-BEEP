package com.lighthouse.presentation.ui.gifticonlist.component

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.presentation.R
import com.lighthouse.presentation.extension.toConcurrency
import com.lighthouse.presentation.extension.toDday
import com.lighthouse.presentation.extension.toExpireDate
import com.lighthouse.presentation.extra.Extras
import com.lighthouse.presentation.ui.detailgifticon.GifticonDetailActivity

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
        }
    ) {
        Row {
            Image(
                painter = rememberAsyncImagePainter(context.getFileStreamPath("cropped${gifticon.id}")),
                contentDescription = stringResource(R.string.gifticon_product_image),
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(cornerSize))
                    .aspectRatio(1f)
                    .align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = gifticon.expireAt.toDday(context),
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
                if (gifticon.isCashCard) {
                    Text(
                        text = gifticon.balance.toConcurrency(context, true),
                        color = colorResource(R.color.beep_pink)
                    )
                }
                Text(
                    text = gifticon.expireAt.toExpireDate(context),
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(bottom = 16.dp, end = 16.dp)
                )
            }
        }
    }
}
