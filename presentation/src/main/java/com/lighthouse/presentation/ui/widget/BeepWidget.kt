package com.lighthouse.presentation.ui.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.unit.ColorProvider
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.presentation.R
import timber.log.Timber
import java.util.Date

class BeepWidget : GlanceAppWidget() {

    private val sampleGifticonItems = listOf(
        Gifticon(
            id = "sample1",
            createdAt = Date(),
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
            createdAt = Date(),
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
        )
    )

    @Composable
    override fun Content() {
        Box(
            modifier = GlanceModifier.fillMaxSize()
                .background(ImageProvider(resId = R.drawable.bg_widget_small_6))
                .appWidgetBackground(),
            contentAlignment = Alignment.Center
        ) {
            WidgetBody()
        }
    }

    @Composable
    fun WidgetBody() {
        Column(
            modifier = GlanceModifier
        ) {
            Row(
                modifier = GlanceModifier.fillMaxWidth().padding(start = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = GlanceModifier.size(12.dp),
                    provider = ImageProvider(resId = R.drawable.ic_marker_market),
                    contentDescription = "Location Icon"
                )
                Spacer(modifier = GlanceModifier.width(4.dp))
                Text(
                    modifier = GlanceModifier.fillMaxWidth().defaultWeight(),
                    text = "테스트",
                    style = TextStyle(
                        fontWeight = FontWeight.Normal,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Start,
                        color = ColorProvider(day = Color.Black, night = Color.White)
                    ),
                    maxLines = 1
                )
                Spacer(modifier = GlanceModifier.width(4.dp))
                Text(
                    modifier = GlanceModifier.size(40.dp).padding(6.dp)
                        .clickable(onClick = actionRunCallback<WidgetRefreshAction>()),
                    text = "새로고침",
                    style = TextStyle(
                        fontWeight = FontWeight.Normal,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Start,
                        color = ColorProvider(day = Color.Black, night = Color.White)
                    ),
                    maxLines = 1
                )
            }
            LazyColumn(modifier = GlanceModifier.padding(8.dp)) {
                items(sampleGifticonItems) { item ->
                    Text(
                        text = item.brand,
                        modifier = GlanceModifier
                            .padding(vertical = 8.dp)
                            .clickable(
                                onClick = actionRunCallback<ListWidgetClickActionCallback>(
                                    actionParametersOf(itemKey to item.id)
                                )
                            )
                    )
                }
            }
        }
    }

    private val itemKey = ActionParameters.Key<String>("itemKey")
}

class WidgetRefreshAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
    }
}

class ListWidgetClickActionCallback : ActionCallback {

    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        Timber.tag("TAG").d("${javaClass.simpleName} Clickable -> $parameters")
    }
}
