package com.example.sudokumobileapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.sudokumobileapp.data.local.entity.SudokuGameEntity
import kotlinx.coroutines.flow.Flow
import androidx.room.OnConflictStrategy
import com.example.sudokumobileapp.data.local.entity.GameRecord

@Dao
interface GameRecordDao {
    @Insert
    suspend fun insert(record: GameRecord)

    @Query("SELECT * FROM gamerecords ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<GameRecord>>

    @Query("DELETE FROM gamerecords")
    suspend fun deleteAll()
}