package com.lighthouse.presentation.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
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
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
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
import com.lighthouse.presentation.extra.Extras.CATEGORY_ACCOMMODATION
import com.lighthouse.presentation.extra.Extras.CATEGORY_CAFE
import com.lighthouse.presentation.extra.Extras.CATEGORY_CONVENIENCE
import com.lighthouse.presentation.extra.Extras.CATEGORY_CULTURE
import com.lighthouse.presentation.extra.Extras.CATEGORY_MART
import com.lighthouse.presentation.extra.Extras.CATEGORY_RESTAURANT
import com.lighthouse.presentation.extra.Extras.KEY_WIDGET_BRAND
import com.lighthouse.presentation.extra.Extras.KEY_WIDGET_EVENT
import com.lighthouse.presentation.extra.Extras.WIDGET_EVENT_MAP
import com.lighthouse.presentation.ui.main.MainActivity
import com.lighthouse.presentation.ui.widget.component.AppWidgetBox
import com.lighthouse.presentation.ui.widget.component.AppWidgetColumn

class BeepWidget : GlanceAppWidget() {

    override val stateDefinition = BeepWidgetInfoStateDefinition

    @Composable
    override fun Content() {
        val widgetState = currentState<WidgetState>()

        MaterialTheme {
            when (widgetState) {
                is WidgetState.Loading -> AppWidgetBox(contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                is WidgetState.Available -> WidgetCommon(widgetState)
                is WidgetState.Unavailable -> WidgetErrorColumn(R.string.widget_common_error)
                is WidgetState.Empty -> WidgetErrorColumn(R.string.widget_empty_data)
                is WidgetState.NoExistsLocationPermission -> WidgetErrorColumn(R.string.widget_has_not_permission)
            }
        }
    }

    @Composable
    private fun WidgetErrorColumn(@StringRes textId: Int) {
        AppWidgetColumn {
            Text(stringResource(id = textId))
            Spacer(modifier = GlanceModifier.width(8.dp))
            Button(
                stringResource(id = R.string.widget_refresh_button),
                actionRunCallback<WidgetRefreshAction>()
            )
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = GlanceModifier.width(4.dp))

            Image(
                modifier = GlanceModifier.size(44.dp).padding(8.dp)
                    .clickable(onClick = actionRunCallback<WidgetRefreshAction>()),
                provider = ImageProvider(resId = R.drawable.ic_splash_beep),
                contentDescription = stringResource(id = R.string.widget_gifticon_image_description)
            )

            Spacer(GlanceModifier.defaultWeight())

            Image(
                modifier = GlanceModifier.size(32.dp).padding(8.dp)
                    .clickable(onClick = actionRunCallback<WidgetRefreshAction>()),
                provider = ImageProvider(resId = R.drawable.ic_widget_refresh_24),
                contentDescription = stringResource(id = R.string.widget_gifticon_image_description)
            )

            Spacer(GlanceModifier.width(10.dp).height(4.dp))
        }
        WidgetBody(widgetState)
    }
}

@Composable
fun WidgetBody(widgetState: WidgetState.Available) {
    Spacer(modifier = GlanceModifier.height(10.dp))
    Text(
        modifier = GlanceModifier.fillMaxWidth(),
        text = stringResource(id = R.string.widget_header_description),
        style = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = ColorProvider(day = Color.Black, night = Color.Black)
        ),
        maxLines = 1
    )
    LazyColumn(modifier = GlanceModifier.padding(12.dp)) {
        items(widgetState.gifticons) { item ->
            GifticonItem(item)
        }
    }
}

@Composable
private fun GifticonItem(item: GifticonWidgetData, modifier: GlanceModifier = GlanceModifier) {
    val intent = Intent(LocalContext.current, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth().padding(8.dp).clickable(
            onClick = actionStartActivity(
                intent,
                parameters = actionParametersOf(
                    ActionParameters.Key<String>(KEY_WIDGET_BRAND) to item.brand,
                    ActionParameters.Key<String>(KEY_WIDGET_EVENT) to WIDGET_EVENT_MAP
                )
            )
        )
    ) {
        Image(
            modifier = GlanceModifier.size(16.dp),
            provider = ImageProvider(resId = getDrawableId(item.category)),
            contentDescription = stringResource(id = R.string.widget_gifticon_image_description)
        )
        Spacer(modifier = GlanceModifier.width(8.dp))
        Text(
            text = item.brand,
            modifier = GlanceModifier.fillMaxWidth().defaultWeight(),
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                textAlign = TextAlign.Start,
                color = ColorProvider(day = Color.Black, night = Color.White)
            )
        )
        Text(
            text = item.name,
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                textAlign = TextAlign.End
            )
        )
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

    override suspend fun onRun(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        BeepWorkManager(context).widgetEnqueue(true)
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun stringResource(@StringRes id: Int) = LocalContext.current.getString(id)
