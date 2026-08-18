package com.mass.notiguard.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.mass.notiguard.R
import com.mass.notiguard.domain.Notifier

/**
 * 前台服务：保持短信 ContentObserver 存活，并在通知栏显示常驻状态。
 * 由 HomeViewModel / BootReceiver 根据「监听总开关」启停。
 */
class GuardForegroundService : Service() {

    private var observer: SmsObserver? = null

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFY_ID, buildNotification())
        observer = SmsObserver(Handler(Looper.getMainLooper()), this)
        contentResolver.registerContentObserver(
            Uri.parse("content://sms"),
            true,
            observer!!
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        observer?.let { contentResolver.unregisterContentObserver(it) }
        observer = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?) = null

    private fun buildNotification(): Notification {
        Notifier.ensureChannel(this)
        return NotificationCompat.Builder(this, Notifier.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("消息哨兵运行中")
            .setContentText("正在监听短信与系统通知")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    companion object {
        const val NOTIFY_ID = 1001
        const val ACTION_STOP = "com.mass.notiguard.action.STOP_GUARD"
    }
}
