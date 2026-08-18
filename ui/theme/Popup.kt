package com.mass.notiguard.ui.theme

import android.content.Context
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.platform.ComposeView
import com.mass.notiguard.ui.theme.SurfaceColor

/** 悬浮窗用的 ComposeView 载体 */
class NotiGuardComposeView(context: Context) : ComposeView(context)

/** 命中关键字时的悬浮弹窗卡片（同时用于悬浮窗与预览） */
@Composable
fun NotiGuardPopupCard(
    appName: String,
    title: String,
    text: String,
    keyword: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Notifications, contentDescription = null, tint = Primary)
                Spacer(Modifier.width(6.dp))
                Text(
                    "命中关键字",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )
                Spacer(Modifier.weight(1f))
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Filled.Close, contentDescription = "关闭", tint = Muted)
                }
            }
            Spacer(Modifier.height(8.dp))
            Surface(
                color = PrimaryContainer,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    " $keyword ",
                    Modifier.padding(6.dp, 3.dp),
                    color = Primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(appName, style = MaterialTheme.typography.labelMedium, color = Muted)
            if (title.isNotBlank()) {
                Text(title, fontWeight = FontWeight.Medium, color = OnSurface)
            }
            Text(
                text.ifBlank { "（无正文）" },
                color = Color(0xFF5A5F6B),
                maxLines = 3
            )
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("知道了")
            }
        }
    }
}
