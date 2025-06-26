package com.example.sudokumobileapp.data.local.entity

// Database table, giống kiểu data class

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sudoku_games")
data class SudokuGameEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String = "CURRENT_GAME", // Luôn ghi đè 1 bản ghi
    val boardJson: String, // Lưu dạng JSON
    val difficulty: String, // độ khó
    val createdAt: Long = System.currentTimeMillis()    // lưu thời điểm tạo
)