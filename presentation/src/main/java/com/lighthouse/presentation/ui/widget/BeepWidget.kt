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
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.lighthouse.presentation.ui.widget.component.AppWidgetBox
import com.lighthouse.presentation.ui.widget.component.AppWidgetColumn
import timber.log.Timber

class BeepWidget : GlanceAppWidget() {

    override val stateDefinition = BeepWidgetInfoStateDefinition

    @Composable
    override fun Content() {
        val widgetState = currentState<WidgetState>()
        Timber.tag("TAG").d("${javaClass.simpleName} Widget state -> $widgetState")

        MaterialTheme {
            when (widgetState) {
                is WidgetState.Loading -> AppWidgetBox(contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                is WidgetState.Available -> WidgetBody(widgetState)
                is WidgetState.Unavailable -> {
                    AppWidgetColumn {
                        Text("데이터가 정상적이지가 않습니다.")
                        Button("새로고침", actionRunCallback<WidgetRefreshAction>())
                    }
                }
                is WidgetState.Empty -> {
                    AppWidgetColumn {
                        Text("근처에 사용 가능한 기프티콘이 존재하지 않습니다.")
                        Button("새로고침", actionRunCallback<WidgetRefreshAction>())
                    }
                }
            }
        }
    }

    @Composable
    fun WidgetBody(widgetState: WidgetState.Available) {
        AppWidgetColumn(
            verticalAlignment = Alignment.Top,
            horizonAlignment = Alignment.Start,
            modifier = GlanceModifier
        ) {
            Row(
                modifier = GlanceModifier.fillMaxWidth().padding(start = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = GlanceModifier.width(4.dp))
                Text(
                    modifier = GlanceModifier.fillMaxWidth().defaultWeight(),
                    text = "근처에서 사용할 수 있는 기프티콘이에요",
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
                items(widgetState.gifticons) { item ->
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
