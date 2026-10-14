package com.mass.notiguard.service

import android.os.Handler
import android.os.Looper
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.mass.notiguard.NotiGuardApplication
import com.mass.notiguard.domain.AlertEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 系统通知监听器：需用户在系统设置中授予「通知使用权」。
 * 监听所有 App 的通知，提取标题/正文后交给 AlertEngine 处理。
 */
class NotificationListener : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn ?: return
        // 跳过自身与持续型通知（如前台服务）
        if (sbn.packageName == packageName) return
        val extras = sbn.notification.extras ?: return
        val title = extras.getCharSequence(android.app.Notification.EXTRA_TITLE)?.toString().orEmpty()
        val text = extras.getCharSequence(android.app.Notification.EXTRA_TEXT)?.toString().orEmpty()
        if (title.isBlank() && text.isBlank()) return

        val appName = runCatching {
            val pm = packageManager
            pm.getApplicationLabel(pm.getApplicationInfo(sbn.packageName, 0)).toString()
        }.getOrDefault(sbn.packageName)

        val engine: AlertEngine = (application as NotiGuardApplication).alertEngine
        CoroutineScope(Dispatchers.IO).launch {
            engine.process(sbn.packageName, appName, title, text)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) = Unit
}
