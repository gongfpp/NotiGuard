package com.mass.notiguard.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mass.notiguard.data.SettingsRepository
import com.mass.notiguard.service.GuardForegroundService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** 开机后若监听总开关开启，则重启前台守卫服务 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
        CoroutineScope(Dispatchers.IO).launch {
            val settings = SettingsRepository(context.applicationContext).getSettings()
            if (settings.monitoringEnabled) {
                ContextCompat.startForegroundService(
                    context,
                    Intent(context, GuardForegroundService::class.java)
                )
            }
        }
    }
}
