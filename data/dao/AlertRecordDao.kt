package com.mass.notiguard.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertRecordDao {
    @Query("SELECT * FROM alert_records ORDER BY time DESC")
    fun getAll(): Flow<List<AlertRecord>>

    @Insert
    suspend fun insert(record: AlertRecord): Long

    @Query("DELETE FROM alert_records")
    suspend fun clear()

    @Delete
    suspend fun delete(record: AlertRecord)
}
