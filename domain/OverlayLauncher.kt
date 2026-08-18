package com.mass.notiguard.domain

import android.content.Context
import android.content.Intent
import com.mass.notiguard.service.AlertOverlayService

/** 启动悬浮窗弹窗服务 */
object OverlayLauncher {
    fun show(context: Context, appName: String, title: String, text: String, keyword: String) {
        val intent = Intent(context, AlertOverlayService::class.java).apply {
            putExtra("appName", appName)
            putExtra("title", title)
            putExtra("text", text)
            putExtra("keyword", keyword)
        }
        runCatching { context.startService(intent) }
    }
}
