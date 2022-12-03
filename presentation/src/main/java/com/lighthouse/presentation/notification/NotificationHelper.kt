package com.lighthouse.presentation.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.lighthouse.presentation.R
import com.lighthouse.presentation.extra.Extras
import com.lighthouse.presentation.ui.detailgifticon.GifticonDetailActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val notificationManager = NotificationManagerCompat.from(context)

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Channel Name"
            val descriptionText = "Channel Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(Extras.NOTIFICATION_CHANNEL, name, importance).apply {
                description = descriptionText
                enableVibration(true)
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun applyNotification(name: String, notifyId: Int) { // 일단 이름만
        val intent = Intent(context, GifticonDetailActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(Extras.KEY_GIFTICON_ID, "ae6d967d-3f98-4c73-a117-5148c5f06b47")
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, Extras.NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.ic_splash_beep)
            .setContentTitle("사용기한이 임박한 기프티콘이 있습니다!")
            .setContentText("$name 의 만료일이 00일 남았습니다")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)

        notificationManager.notify(notifyId, builder.build())
    }
}
