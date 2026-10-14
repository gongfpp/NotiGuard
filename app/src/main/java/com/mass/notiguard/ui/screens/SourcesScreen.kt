package com.mass.notiguard.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mass.notiguard.domain.SourceType
import com.mass.notiguard.ui.components.FlatCard
import com.mass.notiguard.ui.components.SectionTitle
import com.mass.notiguard.ui.components.SwitchRow
import com.mass.notiguard.ui.theme.Muted
import com.mass.notiguard.ui.viewmodel.AppItem
import com.mass.notiguard.ui.viewmodel.SourcesViewModel

@Composable
fun SourcesScreen(vm: SourcesViewModel) {
    var apps by remember { mutableStateOf<List<AppItem>>(emptyList()) }
    val stored by vm.sources.collectAsStateWithLifecycle(initialValue = emptyList())

    LaunchedEffect(Unit) {
        apps = vm.loadInstalledApps()
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("监听来源", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("仅对下方已开启的 App 进行关键字检测（短信在首页设置）", color = Muted)

        SectionTitle("已安装应用（${apps.size}）")
        FlatCard {
            if (apps.isEmpty()) {
                Text("正在读取已安装应用…", color = Muted)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    apps.forEach { app ->
                        val enabled = stored.firstOrNull { it.packageName == app.packageName }?.enabled
                            ?: app.enabled
                        SwitchRow(
                            title = app.appName,
                            subtitle = app.packageName,
                            checked = enabled,
                            onCheckedChange = { v ->
                                vm.setEnabled(app.packageName, app.appName, SourceType.NOTIFICATION, v)
                            }
                        )
                    }
                }
            }
        }
        Spacer(Modifier.padding(bottom = 16.dp))
    }
}
