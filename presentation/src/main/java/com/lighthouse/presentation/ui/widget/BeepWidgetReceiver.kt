package com.lighthouse.presentation.ui.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.lighthouse.presentation.background.BeepWorkManager

class BeepWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget
        get() = BeepWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        BeepWorkManager(context).widgetEnqueue()
    }

    /**
     * 이 친구는 모든 위젯이 없어졌을때 실행될 얘입니다.
     */
    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        BeepWorkManager(context).widgetCancel()
    }
}
