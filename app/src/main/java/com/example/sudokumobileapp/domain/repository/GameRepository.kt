package com.example.sudokumobileapp.domain.repository

import com.example.sudokumobileapp.domain.model.SudokuBoard
import com.example.sudokumobileapp.domain.model.Difficulty
import kotlinx.coroutines.flow.Flow
import com.example.sudokumobileapp.data.local.entity.GameRecord

interface GameRepository {
    suspend fun generateNewBoard(difficulty: Difficulty): SudokuBoard
    suspend fun saveGame(board: SudokuBoard)
    fun loadGame(): Flow<SudokuBoard?>
    suspend fun validateBoard(board: SudokuBoard): Boolean

    fun getGameRecords(): Flow<List<GameRecord>>
    suspend fun saveGameRecord(record: GameRecord)
    suspend fun clearAllRecords()
}