package com.mass.notiguard.domain

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.mass.notiguard.R

/** 系统通知通道与高优提醒通知构造 */
object Notifier {

    const val CHANNEL_ID = "notiguard_alert_channel"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                CHANNEL_ID,
                "关键字提醒",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "匹配到关键字时弹出的提醒通知"
                setBypassDnd(true)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            }
            manager.createNotificationChannel(channel)
        }
    }

    fun post(
        context: Context,
        appName: String,
        title: String,
        text: String,
        keyword: String
    ) {
        ensureChannel(context)
        val launch = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val contentIntent = launch?.let {
            android.app.PendingIntent.getActivity(
                context, 0, it,
                android.app.PendingIntent.FLAG_IMMUTABLE
            )
        }
        val bigText = buildString {
            appendLine(appName)
            if (title.isNotBlank()) appendLine(title)
            append(text)
        }
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("⚡ 命中关键字：$keyword")
            .setContentText("${appName}：${title.ifBlank { text }}")
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .build()
        context.getSystemService(NotificationManager::class.java)
            .notify(System.currentTimeMillis().toInt(), notification)
    }
}
