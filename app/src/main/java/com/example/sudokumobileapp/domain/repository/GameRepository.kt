package com.example.sudokumobileapp.domain.repository

import com.example.sudokumobileapp.domain.model.SudokuBoard
import com.example.sudokumobileapp.domain.model.Difficulty
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    suspend fun generateNewBoard(difficulty: Difficulty): SudokuBoard
    suspend fun saveGame(board: SudokuBoard)
    fun loadSavedGame(): Flow<SudokuBoard?>
    suspend fun validateBoard(board: SudokuBoard): Boolean
}