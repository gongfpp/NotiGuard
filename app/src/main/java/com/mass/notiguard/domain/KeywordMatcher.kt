package com.mass.notiguard.domain

/**
 * 关键字匹配引擎：纯函数、无 Android 依赖，便于单测。
 */
object KeywordMatcher {

    /** 单个关键字是否命中文本 */
    fun match(text: String, keyword: String, mode: MatchMode): Boolean {
        if (text.isBlank() || keyword.isBlank()) return false
        return when (mode) {
            MatchMode.CONTAINS -> text.contains(keyword, ignoreCase = true)
            MatchMode.EXACT -> text.equals(keyword, ignoreCase = true)
            MatchMode.REGEX -> runCatching {
                Regex(keyword, RegexOption.IGNORE_CASE).containsMatchIn(text)
            }.getOrDefault(false)
        }
    }

    /** 在关键字列表中返回第一个命中结果 */
    fun firstMatch(text: String, keywords: List<com.mass.notiguard.data.KeywordRule>): MatchResult {
        for (rule in keywords) {
            if (rule.enabled && match(text, rule.keyword, rule.mode)) {
                return MatchResult(matched = true, keyword = rule.keyword, mode = rule.mode)
            }
        }
        return MatchResult(matched = false)
    }

    data class MatchResult(
        val matched: Boolean,
        val keyword: String = "",
        val mode: MatchMode = MatchMode.CONTAINS
    )
}
