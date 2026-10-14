package com.mass.notiguard.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface KeywordDao {
    @Query("SELECT * FROM keyword_rules ORDER BY id DESC")
    fun getAll(): Flow<List<KeywordRule>>

    @Query("SELECT * FROM keyword_rules WHERE enabled = 1")
    suspend fun getEnabled(): List<KeywordRule>

    @Insert
    suspend fun insert(rule: KeywordRule): Long

    @Update
    suspend fun update(rule: KeywordRule)

    @Delete
    suspend fun delete(rule: KeywordRule)
}
