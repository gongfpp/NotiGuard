package com.mass.notiguard.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [KeywordRule::class, MonitoredSource::class, AlertRecord::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun keywordDao(): KeywordDao
    abstract fun sourceDao(): SourceDao
    abstract fun alertRecordDao(): AlertRecordDao

    companion object {
        fun build(context: Context): AppDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "notiguard.db"
            ).fallbackToDestructiveMigration().build()
    }
}
