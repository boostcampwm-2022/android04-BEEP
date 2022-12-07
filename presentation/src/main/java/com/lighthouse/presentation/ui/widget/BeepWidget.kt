package com.lighthouse.presentation.ui.widget

import android.content.Context
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.unit.ColorProvider
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.lighthouse.presentation.R
import com.lighthouse.presentation.background.BeepWorkManager
import com.lighthouse.presentation.ui.widget.component.AppWidgetBox
import com.lighthouse.presentation.ui.widget.component.AppWidgetColumn
import com.lighthouse.presentation.util.CATEGORY_ACCOMMODATION
import com.lighthouse.presentation.util.CATEGORY_CAFE
import com.lighthouse.presentation.util.CATEGORY_CONVENIENCE
import com.lighthouse.presentation.util.CATEGORY_CULTURE
import com.lighthouse.presentation.util.CATEGORY_MART
import com.lighthouse.presentation.util.CATEGORY_RESTAURANT
import timber.log.Timber

class BeepWidget : GlanceAppWidget() {

    override val stateDefinition = BeepWidgetInfoStateDefinition

    @Composable
    override fun Content() {
        val widgetState = currentState<WidgetState>()

        MaterialTheme {
            when (widgetState) {
                is WidgetState.Loading -> AppWidgetBox(contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                is WidgetState.Available -> WidgetCommon(widgetState)
                is WidgetState.Unavailable -> {
                    AppWidgetColumn {
                        Text("데이터가 정상적이지가 않습니다.")
                        Spacer(modifier = GlanceModifier.width(8.dp))
                        Button("새로고침", actionRunCallback<WidgetRefreshAction>())
                    }
                }
                is WidgetState.Empty -> {
                    AppWidgetColumn {
                        Text("근처에 사용 가능한 기프티콘이 존재하지 않습니다.")
                        Spacer(modifier = GlanceModifier.width(8.dp))
                        Button("새로고침", actionRunCallback<WidgetRefreshAction>())
                    }
                }
                is WidgetState.NoExistsLocationPermission -> {
                    AppWidgetColumn {
                        Text("권한을 허용하신 후 다시 새로고침 해주세요.")
                        Spacer(modifier = GlanceModifier.width(8.dp))
                        Button("권한 허용", actionRunCallback<WidgetRefreshAction>())
                        Spacer(modifier = GlanceModifier.width(4.dp))
                        Button("새로고침", actionRunCallback<WidgetRefreshAction>())
                    }
                }
            }
        }
    }
}

@Composable
fun WidgetCommon(widgetState: WidgetState.Available) {
    AppWidgetColumn(
        verticalAlignment = Alignment.Top,
        horizonAlignment = Alignment.Start,
        modifier = GlanceModifier
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth().background(R.color.beep_pink).height(40.dp).padding(start = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = GlanceModifier.width(4.dp))
            Text(
                modifier = GlanceModifier.fillMaxWidth().defaultWeight(),
                text = "근처에서 사용할 수 있는 기프티콘이에요",
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Start,
                    color = ColorProvider(day = Color.White, night = Color.White)
                ),
                maxLines = 1
            )
            Spacer(modifier = GlanceModifier.width(6.dp))
            Image(
                modifier = GlanceModifier.size(32.dp).padding(8.dp)
                    .clickable(onClick = actionRunCallback<WidgetRefreshAction>()),
                provider = ImageProvider(resId = R.drawable.ic_widget_refresh_24),
                contentDescription = "Refresh icon"
            )
        }
        WidgetBody(widgetState)
    }
}

val itemKey = ActionParameters.Key<String>("itemKey")

@Composable
fun WidgetBody(widgetState: WidgetState.Available) {
    LazyColumn(modifier = GlanceModifier.padding(12.dp)) {
        items(widgetState.gifticons) { item ->
            Spacer(modifier = GlanceModifier.width(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = GlanceModifier.clickable(
                    onClick = actionRunCallback<ListWidgetClickActionCallback>(
                        actionParametersOf(itemKey to item.id)
                    )
                ).fillMaxWidth()
            ) {
                Image(
                    provider = ImageProvider(resId = getDrawableId(item.category)),
                    contentDescription = "brand image"
                )
                Spacer(modifier = GlanceModifier.width(4.dp))
                Text(
                    text = item.brand,
                    modifier = GlanceModifier.fillMaxWidth().defaultWeight().padding(vertical = 8.dp),
                    style = TextStyle(
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Start,
                        color = ColorProvider(day = Color.Black, night = Color.Black)
                    )
                )
                Text(
                    text = item.name,
                    modifier = GlanceModifier.padding(vertical = 8.dp),
                    style = TextStyle(
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        textAlign = TextAlign.End
                    )
                )
            }
        }
    }
}

fun getDrawableId(category: String): Int {
    return when (category) {
        CATEGORY_MART -> R.drawable.ic_widget_market
        CATEGORY_CONVENIENCE -> R.drawable.ic_widget_convenience
        CATEGORY_CULTURE -> R.drawable.ic_widget_culture
        CATEGORY_ACCOMMODATION -> R.drawable.ic_widget_accommodation
        CATEGORY_RESTAURANT -> R.drawable.ic_widget_restaurant
        CATEGORY_CAFE -> R.drawable.ic_widget_cafe
        else -> R.drawable.ic_marker_base
    }
}

class WidgetRefreshAction : ActionCallback {

    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        BeepWorkManager(context).widgetEnqueue(true)
    }
}

class ListWidgetClickActionCallback : ActionCallback {

    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        Timber.tag("TAG").d("${javaClass.simpleName} Clickable -> $parameters")
    }
}
