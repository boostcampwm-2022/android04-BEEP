package com.lighthouse.presentation.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.unit.ColorProvider
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
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
import com.lighthouse.presentation.ui.widget.component.AppWidgetColumn
import timber.log.Timber

class BeepWidget : GlanceAppWidget() {

    override val stateDefinition = BeepWidgetInfoStateDefinition

    override val sizeMode: SizeMode
        get() = SizeMode.Responsive(setOf(thinMode, smallMode, mediumMode, largeMode))

    @Composable
    override fun Content() {
        val widgetState = currentState<WidgetState>()
        val size = LocalSize.current
        AppWidgetColumn(
            verticalAlignment = Alignment.Top,
            horizonAlignment = Alignment.Start
        ) {
            WidgetHead()
            when (widgetState) {
                is WidgetState.Loading -> WidgetLoading()
                is WidgetState.Available -> {
                    Timber.tag("TAG").d("${javaClass.simpleName} size -> $size")
                    when (size) {
                        thinMode -> WidgetBody(widgetState, false)
                        else -> WidgetBody(widgetState, true)
                    }
                }
                is WidgetState.Unavailable -> WidgetErrorBody(R.string.widget_common_error)
                is WidgetState.Empty -> WidgetErrorBody(R.string.widget_empty_data)
                is WidgetState.NoExistsLocationPermission -> WidgetErrorBody(R.string.widget_has_not_permission)
            }
        }
    }

    companion object {
        private val thinMode = DpSize(120.dp, 80.dp)
        private val smallMode = DpSize(184.dp, 120.dp)
        private val mediumMode = DpSize(260.dp, 200.dp)
        private val largeMode = DpSize(260.dp, 280.dp)
    }
}

@Composable
fun WidgetLoading() {
    Box(
        modifier = GlanceModifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun WidgetHead() {
    val intent = Intent(LocalContext.current, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    Row(
        modifier = GlanceModifier.fillMaxWidth().background(R.color.beep_pink).height(40.dp).padding(start = 12.dp)
            .clickable(actionStartActivity(intent)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = GlanceModifier.width(4.dp))

        Image(
            modifier = GlanceModifier.size(48.dp).padding(8.dp)
                .clickable(onClick = actionRunCallback<WidgetRefreshAction>()),
            provider = ImageProvider(resId = R.drawable.ic_splash_beep),
            contentDescription = stringResource(id = R.string.widget_gifticon_image_description)
        )

        Spacer(GlanceModifier.defaultWeight())

        Image(
            modifier = GlanceModifier.size(52.dp).padding(start = 16.dp, end = 16.dp)
                .clickable(onClick = actionRunCallback<WidgetRefreshAction>()),
            provider = ImageProvider(resId = R.drawable.ic_widget_refresh_24),
            contentDescription = stringResource(id = R.string.widget_gifticon_image_description)
        )
        Spacer(GlanceModifier.width(12.dp))
    }
}

@Composable
fun WidgetErrorBody(@StringRes textId: Int) {
    Box(
        modifier = GlanceModifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            stringResource(id = textId),
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = ColorProvider(day = Color.Black, night = Color.White)
            )
        )
    }
}

@Composable
fun WidgetBody(widgetState: WidgetState.Available, isShowGifticon: Boolean) {
    Spacer(modifier = GlanceModifier.height(10.dp))
    Text(
        modifier = GlanceModifier.fillMaxWidth(),
        text = stringResource(id = R.string.widget_header_description),
        style = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = ColorProvider(day = Color.Black, night = Color.White)
        ),
        maxLines = 1
    )
    LazyColumn(modifier = GlanceModifier.padding(12.dp)) {
        items(widgetState.gifticons.toList()) { item ->
            GifticonItem(item, isShowGifticon)
        }
    }
}

@Composable
private fun GifticonItem(
    item: Pair<Map.Entry<String, String>, Int?>,
    isShowGifticon: Boolean,
    modifier: GlanceModifier = GlanceModifier
) {
    val brandWithCategory = item.first
    val gifticonCount = item.second ?: return

    val intent = Intent(LocalContext.current, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    Box() {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.fillMaxWidth().padding(start = 24.dp, top = 10.dp, end = 24.dp, bottom = 10.dp)
        ) {
            Image(
                modifier = GlanceModifier.size(16.dp),
                provider = ImageProvider(resId = getDrawableId(brandWithCategory.value)),
                contentDescription = stringResource(id = R.string.widget_gifticon_image_description)
            )
            Spacer(modifier = GlanceModifier.width(8.dp))
            Text(
                text = brandWithCategory.key,
                style = TextStyle(
                    fontSize = 14.sp,
                    textAlign = TextAlign.Start,
                    color = ColorProvider(day = Color.Black, night = Color.White)
                )
            )
            Spacer(GlanceModifier.defaultWeight())
            if (isShowGifticon) {
                Text(
                    text = stringResource(R.string.widget_gifticon_count, gifticonCount),
                    style = TextStyle(
                        fontSize = 14.sp,
                        textAlign = TextAlign.End
                    )
                )
            }
        }
        Spacer(
            modifier = modifier.fillMaxSize().clickable(onClick = startActivityHome(intent, brandWithCategory))
        )
    }
}

@Composable
private fun startActivityHome(
    intent: Intent,
    brandWithCategory: Map.Entry<String, String>
) = actionStartActivity(
    intent,
    parameters = actionParametersOf(
        ActionParameters.Key<String>(KEY_WIDGET_BRAND) to brandWithCategory.key,
        ActionParameters.Key<String>(KEY_WIDGET_EVENT) to WIDGET_EVENT_MAP
    )
)

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

@SuppressLint("ComposableNaming")
@Composable
fun stringResource(@StringRes id: Int, vararg args: Any) = LocalContext.current.getString(id, *args)
