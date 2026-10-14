package com.mass.notiguard.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SourceDao {
    @Query("SELECT * FROM monitored_sources")
    fun getAll(): Flow<List<MonitoredSource>>

    @Query("SELECT * FROM monitored_sources WHERE enabled = 1")
    suspend fun getEnabledList(): List<MonitoredSource>

    @Query("SELECT * FROM monitored_sources WHERE packageName = :pkg")
    suspend fun get(pkg: String): MonitoredSource?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(source: MonitoredSource)

    @Update
    suspend fun update(source: MonitoredSource)

    @Delete
    suspend fun delete(source: MonitoredSource)
}
