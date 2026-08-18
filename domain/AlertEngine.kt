package com.mass.notiguard.domain

import android.content.Context
import android.provider.Settings
import com.mass.notiguard.data.AlertRecord
import com.mass.notiguard.data.AppDatabase
import com.mass.notiguard.data.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 提醒引擎：统一处理「来源 -> 关键字匹配 -> 记录 + 多渠道提醒」。
 * source 取值：包名（通知）/ "sms"（短信）。
 */
class AlertEngine(
    private val context: Context,
    private val db: AppDatabase,
    private val settings: SettingsRepository
) {
    suspend fun process(
        source: String,
        appName: String,
        title: String,
        text: String
    ) = withContext(Dispatchers.IO) {
        val s = settings.getSettings()
        if (!s.monitoringEnabled) return@withContext

        val isSms = source == "sms"
        if (isSms) {
            if (!s.monitorSms) return@withContext
        } else {
            val enabled = db.sourceDao().getEnabledList()
            if (!enabled.any { it.packageName == source }) return@withContext
        }

        val keywords = db.keywordDao().getEnabled()
        if (keywords.isEmpty()) return@withContext

        val result = KeywordMatcher.firstMatch("$title $text", keywords)
        if (!result.matched) return@withContext

        db.alertRecordDao().insert(
            AlertRecord(
                time = System.currentTimeMillis(),
                source = appName,
                title = title,
                matchedText = text,
                keyword = result.keyword
            )
        )

        if (s.alertNotification) Notifier.post(context, appName, title, text, result.keyword)
        if (s.alertPopup && Settings.canDrawOverlays(context)) {
            OverlayLauncher.show(context, appName, title, text, result.keyword)
        }
        if (s.alertVibrate) Ringer.vibrate(context)
        if (s.alertSound) Ringer.sound(context)
    }
}
