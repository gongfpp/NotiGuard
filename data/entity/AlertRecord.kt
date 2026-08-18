package com.mass.notiguard.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/** 命中关键字的提醒记录（历史） */
@Entity(tableName = "alert_records")
data class AlertRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val time: Long,
    val source: String,
    val title: String,
    val matchedText: String,
    val keyword: String
)
