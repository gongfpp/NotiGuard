package com.mass.notiguard.domain

/** 关键字匹配模式 */
enum class MatchMode {
    /** 包含（忽略大小写） */
    CONTAINS,
    /** 完全相等（忽略大小写） */
    EXACT,
    /** 正则匹配 */
    REGEX
}

/** 监听来源类型 */
enum class SourceType {
    /** 系统通知 */
    NOTIFICATION,
    /** 短信（全局，不区分 App） */
    SMS
}
