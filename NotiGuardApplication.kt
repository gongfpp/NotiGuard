package com.mass.notiguard

import android.app.Application
import com.mass.notiguard.data.AppDatabase
import com.mass.notiguard.data.SettingsRepository
import com.mass.notiguard.domain.AlertEngine

class NotiGuardApplication : Application() {

    val database by lazy { AppDatabase.build(this) }

    val settings by lazy { SettingsRepository(this) }

    val alertEngine by lazy { AlertEngine(this, database, settings) }
}
