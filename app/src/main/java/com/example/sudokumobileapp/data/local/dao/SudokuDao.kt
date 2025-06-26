package com.example.sudokumobileapp.data.local.dao

// # dùng datastore để lưu dữ liệu
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.sudokumobileapp.data.local.entity.SudokuGameEntity
import kotlinx.coroutines.flow.Flow
import androidx.room.OnConflictStrategy

@Dao
interface SudokuGameDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: SudokuGameEntity)

    @Query("DELETE FROM sudoku_games WHERE id = 'CURRENT_GAME'")
    suspend fun deleteCurrentGame()

    @Query("SELECT * FROM sudoku_games WHERE id = 'CURRENT_GAME' LIMIT 1")
    fun observeCurrentGame(): Flow<SudokuGameEntity?>
}