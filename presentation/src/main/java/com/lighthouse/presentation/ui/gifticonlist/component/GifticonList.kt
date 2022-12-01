package com.lighthouse.presentation.ui.gifticonlist.component

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.presentation.R
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
