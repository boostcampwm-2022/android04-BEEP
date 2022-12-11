package com.lighthouse.presentation.background

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.presentation.R
import com.lighthouse.presentation.extra.Extras
import com.lighthouse.presentation.ui.detailgifticon.GifticonDetailActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val notificationManager = NotificationManagerCompat.from(context)

    private val channelDescription = context.getString(R.string.notification_channel_description)
    private val channelName = "기프티콘 만료 알림"
    private val channelGroup = "BEEP"

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = channelName
            val descriptionText = channelDescription
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(Extras.NOTIFICATION_CHANNEL, name, importance).apply {
                description = descriptionText
                enableVibration(true)
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun applyNotification(gifticon: Gifticon, remainDays: Int) {
        val intent = Intent(context, GifticonDetailActivity::class.java).apply {
            putExtra(Extras.KEY_GIFTICON_ID, gifticon.id)
        }

        val code = gifticon.barcode.hashCode()

        val gifticonDetailPendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(code, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val builder = NotificationCompat.Builder(context, Extras.NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.ic_splash_beep)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(
                String.format(
                    context.getString(R.string.notification_description),
                    gifticon.name,
                    remainDays
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(gifticonDetailPendingIntent)
            .setGroup(channelGroup)

        val notification = builder.build().apply {
            flags = Notification.FLAG_AUTO_CANCEL
        }
        notificationManager.notify(code, notification)
    }
}
