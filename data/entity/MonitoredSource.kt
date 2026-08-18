package com.mass.notiguard.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mass.notiguard.domain.SourceType

/** 被监听的来源（按 App 包名维度）；短信为特殊全局来源 */
@Entity(tableName = "monitored_sources")
data class MonitoredSource(
    @PrimaryKey val packageName: String,
    val appName: String,
    val enabled: Boolean = true,
    val sourceType: SourceType
)
