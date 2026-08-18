package com.mass.notiguard.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mass.notiguard.data.KeywordRule
import com.mass.notiguard.domain.MatchMode
import com.mass.notiguard.ui.components.FlatCard
import com.mass.notiguard.ui.components.SectionTitle
import com.mass.notiguard.ui.theme.Muted
import com.mass.notiguard.ui.theme.Primary
import com.mass.notiguard.ui.theme.SurfaceColor
import com.mass.notiguard.ui.viewmodel.KeywordsViewModel

@Composable
fun KeywordsScreen(vm: KeywordsViewModel) {
    val rules by vm.rules.collectAsStateWithLifecycle(initialValue = emptyList())
    var dialogOpen by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Text("关键字列表", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("命中任意一个关键字即触发提醒", color = Muted)

            SectionTitle("已配置（${rules.size}）")
            LazyColumn {
                items(rules) { rule ->
                    FlatCard(Modifier.padding(bottom = 10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(rule.keyword, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                                Text(
                                    when (rule.mode) {
                                        MatchMode.CONTAINS -> "包含匹配"
                                        MatchMode.EXACT -> "完全相等"
                                        MatchMode.REGEX -> "正则表达式"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Muted
                                )
                            }
                            Switch(
                                checked = rule.enabled,
                                onCheckedChange = { vm.toggle(rule) },
                                colors = SwitchDefaults.colors(checkedTrackColor = Primary, checkedThumbColor = SurfaceColor)
                            )
                            IconButton(onClick = { vm.delete(rule) }) {
                                Icon(Icons.Filled.Delete, contentDescription = "删除", tint = Muted)
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { dialogOpen = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            containerColor = Primary
        ) { Icon(Icons.Filled.Add, contentDescription = "新增关键字") }
    }

    if (dialogOpen) {
        AddKeywordDialog(
            onDismiss = { dialogOpen = false },
            onConfirm = { kw, mode -> vm.add(kw, mode); dialogOpen = false }
        )
    }
}

@Composable
private fun AddKeywordDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, MatchMode) -> Unit
) {
    var text by remember { mutableStateOf("") }
    var mode by remember { mutableStateOf(MatchMode.CONTAINS) }
    val modes = listOf(MatchMode.CONTAINS, MatchMode.EXACT, MatchMode.REGEX)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SurfaceColor,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(Modifier.padding(20.dp)) {
                Text("新增关键字", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.padding(8.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("关键字内容") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.padding(8.dp))
                Text("匹配方式", style = MaterialTheme.typography.labelMedium, color = Muted)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    modes.forEach { m ->
                        val selected = mode == m
                        val label = when (m) {
                            MatchMode.CONTAINS -> "包含"
                            MatchMode.EXACT -> "相等"
                            MatchMode.REGEX -> "正则"
                        }
                        TextButton(
                            onClick = { mode = m },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = if (selected) SurfaceColor else Primary,
                                containerColor = if (selected) Primary else SurfaceColor
                            )
                        ) { Text(label) }
                    }
                }
                Spacer(Modifier.padding(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("取消", color = Muted) }
                    TextButton(onClick = { onConfirm(text, mode) }) { Text("保存", color = Primary) }
                }
            }
        }
    }
}
