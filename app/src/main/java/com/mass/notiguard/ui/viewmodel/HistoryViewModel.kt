package com.mass.notiguard.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mass.notiguard.NotiGuardApplication
import com.mass.notiguard.data.AlertRecord
import com.mass.notiguard.data.AlertRecordDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class HistoryViewModel(app: Application) : AndroidViewModel(app) {

    private val dao: AlertRecordDao = (app as NotiGuardApplication).database.alertRecordDao()

    val records: Flow<List<AlertRecord>> = dao.getAll()

    fun clear() {
        viewModelScope.launch(Dispatchers.IO) { dao.clear() }
    }

    fun delete(record: AlertRecord) {
        viewModelScope.launch(Dispatchers.IO) { dao.delete(record) }
    }
}
