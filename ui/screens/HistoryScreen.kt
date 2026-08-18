package com.mass.notiguard.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mass.notiguard.data.AlertRecord
import com.mass.notiguard.ui.components.FlatCard
import com.mass.notiguard.ui.components.SectionTitle
import com.mass.notiguard.ui.theme.Muted
import com.mass.notiguard.ui.theme.Primary
import com.mass.notiguard.ui.theme.PrimaryContainer
import com.mass.notiguard.ui.theme.SurfaceColor
import com.mass.notiguard.ui.viewmodel.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(vm: HistoryViewModel) {
    val records by vm.records.collectAsStateWithLifecycle(initialValue = emptyList())
    var confirmClear by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("命中记录", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("共 ${records.size} 条", color = Muted)
            }
            if (records.isNotEmpty()) {
                OutlinedButton(onClick = { confirmClear = true }) {
                    Text("清空", color = Primary)
                }
            }
        }

        SectionTitle("时间线")
        LazyColumn {
            items(records) { record ->
                FlatCard(Modifier.padding(bottom = 10.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(fmt(record.time), style = MaterialTheme.typography.labelSmall, color = Muted)
                            Spacer(Modifier.weight(1f))
                            Surface(
                                color = PrimaryContainer, shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(" ${record.keyword} ", Modifier.padding(6.dp, 2.dp), color = Primary, fontWeight = FontWeight.Bold)
                            }
                            IconButton(onClick = { vm.delete(record) }) {
                                Icon(Icons.Filled.Delete, contentDescription = "删除", tint = Muted)
                            }
                        }
                        Text(record.source, style = MaterialTheme.typography.labelMedium, color = Muted)
                        if (record.title.isNotBlank()) {
                            Text(record.title, fontWeight = FontWeight.Medium)
                        }
                        Text(record.matchedText.ifBlank { "（无正文）" }, color = androidx.compose.ui.graphics.Color(0xFF5A5F6B))
                    }
                }
            }
        }
    }

    if (confirmClear) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { confirmClear = false }) {
            androidx.compose.material3.Surface(
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                color = SurfaceColor,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("确认清空全部记录？", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    androidx.compose.foundation.layout.Spacer(Modifier.padding(12.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        androidx.compose.material3.TextButton(onClick = { confirmClear = false }) {
                            Text("取消", color = Muted)
                        }
                        androidx.compose.material3.TextButton(onClick = { vm.clear(); confirmClear = false }) {
                            Text("清空", color = Primary)
                        }
                    }
                }
            }
        }
    }
}

private fun fmt(time: Long): String =
    SimpleDateFormat("MM-dd HH:mm:ss", Locale.CHINA).format(Date(time))
