package com.mass.notiguard.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mass.notiguard.NotiGuardApplication
import com.mass.notiguard.data.MonitoredSource
import com.mass.notiguard.data.SourceDao
import com.mass.notiguard.domain.SourceType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class AppItem(
    val packageName: String,
    val appName: String,
    val enabled: Boolean
)

class SourcesViewModel(app: Application) : AndroidViewModel(app) {

    private val dao: SourceDao = (app as NotiGuardApplication).database.sourceDao()

    val sources: Flow<List<MonitoredSource>> = dao.getAll()

    fun setEnabled(pkg: String, name: String, type: SourceType, enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.upsert(MonitoredSource(pkg, name, enabled, type))
        }
    }

    /** 读取已安装的可启动 App，并与已保存的启用状态合并 */
    suspend fun loadInstalledApps(): List<AppItem> = withContext(Dispatchers.IO) {
        val pm = getApplication<Application>().packageManager
        val stored = mutableMapOf<String, Boolean>()
        dao.getAll().first().associateTo(stored) { it.packageName to it.enabled }

        val intent = android.content.Intent(android.content.Intent.ACTION_MAIN, null).apply {
            addCategory(android.content.Intent.CATEGORY_LAUNCHER)
        }
        pm.queryIntentActivities(intent, 0)
            .map { it.activityInfo.applicationInfo }
            .distinctBy { it.packageName }
            .filter { it.packageName != getApplication<Application>().packageName }
            .map { ai ->
                AppItem(
                    packageName = ai.packageName,
                    appName = pm.getApplicationLabel(ai).toString(),
                    enabled = stored[ai.packageName] ?: true
                )
            }
            .sortedBy { it.appName }
    }
}
