package com.mass.notiguard.ui.viewmodel

import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mass.notiguard.data.Settings
import com.mass.notiguard.data.SettingsRepository
import com.mass.notiguard.service.GuardForegroundService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class HomeViewModel(app: android.app.Application) : AndroidViewModel(app) {

    private val repo = SettingsRepository(app)

    val settings: Flow<Settings> = repo.settingsFlow

    private fun setEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repo.update { it.copy(monitoringEnabled = enabled) }
            val intent = android.content.Intent(getApplication(), GuardForegroundService::class.java)
            if (enabled) {
                androidx.core.content.ContextCompat.startForegroundService(getApplication(), intent)
            } else {
                getApplication<android.app.Application>().startService(
                    intent.apply { action = GuardForegroundService.ACTION_STOP }
                )
            }
        }
    }

    fun setMonitoring(v: Boolean) = setEnabled(v)
    fun setMonitorSms(v: Boolean) = viewModelScope.launch { repo.update { it.copy(monitorSms = v) } }
    fun setAlertNotification(v: Boolean) = viewModelScope.launch { repo.update { it.copy(alertNotification = v) } }
    fun setAlertPopup(v: Boolean) = viewModelScope.launch { repo.update { it.copy(alertPopup = v) } }
    fun setAlertSound(v: Boolean) = viewModelScope.launch { repo.update { it.copy(alertSound = v) } }
    fun setAlertVibrate(v: Boolean) = viewModelScope.launch { repo.update { it.copy(alertVibrate = v) } }
}
