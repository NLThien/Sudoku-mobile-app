package com.example.sudokumobileapp.data.mapper

import com.example.sudokumobileapp.data.local.entity.SudokuGameEntity
import com.example.sudokumobileapp.domain.model.SudokuBoard
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class SudokuMapper {
    private val json = Json { ignoreUnknownKeys = true }

    fun toEntity(board: SudokuBoard): SudokuGameEntity {
        return SudokuGameEntity(
            boardJson = json.encodeToString(board.cells),
            difficulty = board.difficulty.name
        )
    }

    fun toDomain(entity: SudokuGameEntity): SudokuBoard {
        return SudokuBoard(
            cells = json.decodeFromString(entity.boardJson),
            difficulty = enumValueOf(entity.difficulty)
        )
    }
}