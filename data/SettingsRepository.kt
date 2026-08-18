package com.mass.notiguard.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/** 全局提醒与监听开关配置（DataStore Preferences） */
data class Settings(
    val monitoringEnabled: Boolean = false,
    val monitorSms: Boolean = true,
    val alertNotification: Boolean = true,
    val alertPopup: Boolean = true,
    val alertSound: Boolean = false,
    val alertVibrate: Boolean = true
)

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "notiguard_settings")

private object Keys {
    val MONITORING = booleanPreferencesKey("monitoring_enabled")
    val SMS = booleanPreferencesKey("monitor_sms")
    val NOTIF = booleanPreferencesKey("alert_notification")
    val POPUP = booleanPreferencesKey("alert_popup")
    val SOUND = booleanPreferencesKey("alert_sound")
    val VIBRATE = booleanPreferencesKey("alert_vibrate")
}

class SettingsRepository(context: Context) {

    private val store = context.applicationContext.dataStore

    val settingsFlow: Flow<Settings> = store.data.map { prefs ->
        Settings(
            monitoringEnabled = prefs[Keys.MONITORING] ?: false,
            monitorSms = prefs[Keys.SMS] ?: true,
            alertNotification = prefs[Keys.NOTIF] ?: true,
            alertPopup = prefs[Keys.POPUP] ?: true,
            alertSound = prefs[Keys.SOUND] ?: false,
            alertVibrate = prefs[Keys.VIBRATE] ?: true
        )
    }

    suspend fun getSettings(): Settings = settingsFlow.first()

    suspend fun update(transform: (Settings) -> Settings) {
        val next = transform(settingsFlow.first())
        store.edit { prefs ->
            prefs[Keys.MONITORING] = next.monitoringEnabled
            prefs[Keys.SMS] = next.monitorSms
            prefs[Keys.NOTIF] = next.alertNotification
            prefs[Keys.POPUP] = next.alertPopup
            prefs[Keys.SOUND] = next.alertSound
            prefs[Keys.VIBRATE] = next.alertVibrate
        }
    }
}
