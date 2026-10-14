package com.mass.notiguard.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mass.notiguard.domain.MatchMode

/** 用户自定义关键字规则 */
@Entity(tableName = "keyword_rules")
data class KeywordRule(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val keyword: String,
    val mode: MatchMode,
    val enabled: Boolean = true,
    val note: String = ""
)
