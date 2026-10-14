package com.mass.notiguard.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import com.mass.notiguard.ui.components.FlatCard
import com.mass.notiguard.ui.components.SectionTitle
import com.mass.notiguard.ui.components.SwitchRow
import com.mass.notiguard.ui.theme.Muted
import com.mass.notiguard.ui.theme.OnSurface
import com.mass.notiguard.ui.theme.Primary
import com.mass.notiguard.ui.theme.PrimaryContainer
import com.mass.notiguard.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(vm: HomeViewModel) {
    val context = LocalContext.current
    val s by vm.settings.collectAsState(initial = null)
    val listenerGranted = isNotificationListenerEnabled(context)
    val overlayGranted = Settings.canDrawOverlays(context)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("消息哨兵", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("实时监听通知与短信，命中关键字立即提醒", color = Muted)

        Spacer(Modifier.height(16.dp))

        // 监听总开关
        FlatCard {
            SwitchRow(
                title = "监听总开关",
                subtitle = if (s?.monitoringEnabled == true) "正在监听" else "已暂停",
                checked = s?.monitoringEnabled ?: false,
                onCheckedChange = vm::setMonitoring
            )
        }

        // 权限引导
        SectionTitle("权限授权")
        FlatCard {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PermissionHint(
                    granted = listenerGranted,
                    label = "通知监听权限",
                    actionLabel = "去开启",
                    onClick = {
                        context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                    }
                )
                PermissionHint(
                    granted = overlayGranted,
                    label = "悬浮窗权限（应用内弹窗）",
                    actionLabel = "去开启",
                    onClick = {
                        context.startActivity(
                            Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:${context.packageName}")
                            )
                        )
                    }
                )
            }
        }

        // 监听来源
        SectionTitle("监听来源")
        FlatCard {
            SwitchRow(
                title = "短信",
                subtitle = "读取收件箱新短信",
                checked = s?.monitorSms ?: true,
                onCheckedChange = vm::setMonitorSms
            )
        }

        // 提醒方式
        SectionTitle("提醒方式")
        FlatCard {
            SwitchRow("系统通知", "高优先级通知栏提醒", s?.alertNotification ?: true, vm::setAlertNotification)
            SwitchRow("应用内弹窗", "悬浮窗即时弹窗", s?.alertPopup ?: true, vm::setAlertPopup)
            SwitchRow("声音", "播放提示音", s?.alertSound ?: false, vm::setAlertSound)
            SwitchRow("震动", "震动反馈", s?.alertVibrate ?: true, vm::setAlertVibrate)
        }

        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun PermissionHint(
    granted: Boolean,
    label: String,
    actionLabel: String,
    onClick: () -> Unit
) {
    val color = if (granted) Primary else Muted
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = OnSurface)
            Text(
                if (granted) "已授权" else "未授权，点击前往系统设置",
                style = MaterialTheme.typography.bodySmall,
                color = color
            )
        }
        if (!granted) {
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer, contentColor = Primary),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp)
            ) { Text(actionLabel) }
        }
    }
}

private fun isNotificationListenerEnabled(context: Context): Boolean =
    NotificationManagerCompat.getEnabledListenerPackages(context)
        .contains(context.packageName)
