package com.mass.notiguard.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mass.notiguard.NotiGuardApplication
import com.mass.notiguard.data.KeywordDao
import com.mass.notiguard.data.KeywordRule
import com.mass.notiguard.domain.MatchMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class KeywordsViewModel(app: Application) : AndroidViewModel(app) {

    private val dao: KeywordDao = (app as NotiGuardApplication).database.keywordDao()

    val rules: Flow<List<KeywordRule>> = dao.getAll()

    fun add(keyword: String, mode: MatchMode) {
        val kw = keyword.trim()
        if (kw.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            dao.insert(KeywordRule(keyword = kw, mode = mode))
        }
    }

    fun toggle(rule: KeywordRule) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.update(rule.copy(enabled = !rule.enabled))
        }
    }

    fun delete(rule: KeywordRule) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.delete(rule)
        }
    }
}
