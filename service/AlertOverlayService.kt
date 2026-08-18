package com.mass.notiguard.service

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.mass.notiguard.ui.theme.LightColors
import com.mass.notiguard.ui.theme.NotiGuardPopupCard

/**
 * 命中关键字时弹出的悬浮窗（需 SYSTEM_ALERT_WINDOW 权限）。
 * 顶部出现卡片，8 秒后或点击「知道了」自动消失。
 */
class AlertOverlayService : Service() {

    private var windowManager: WindowManager? = null
    private var view: android.view.View? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val appName = intent?.getStringExtra("appName").orEmpty()
        val title = intent?.getStringExtra("title").orEmpty()
        val text = intent?.getStringExtra("text").orEmpty()
        val keyword = intent?.getStringExtra("keyword").orEmpty()
        showOverlay(appName, title, text, keyword)
        return START_NOT_STICKY
    }

    private fun showOverlay(appName: String, title: String, text: String, keyword: String) {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.TOP }

        val composeView = com.mass.notiguard.ui.theme.NotiGuardComposeView(this).apply {
            setContent {
                MaterialTheme(colorScheme = LightColors) {
                    NotiGuardPopupCard(appName, title, text, keyword) { dismiss() }
                }
            }
        }
        try {
            windowManager?.addView(composeView, params)
            view = composeView
            Handler(Looper.getMainLooper()).postDelayed({ dismiss() }, 8000)
        } catch (_: Exception) {
            stopSelf()
        }
    }

    private fun dismiss() {
        view?.let { windowManager?.removeView(it) }
        view = null
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
