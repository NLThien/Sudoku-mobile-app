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
@Entity(tableName = "gamerecords")
data class GameRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val mode: String,          // "free" hoặc "challenge"
    val difficulty: String,    // "Dễ", "Trung bình", "Khó"
    val completionTime: Long,  // Thời gian hoàn thành (giây)
    val errorCount: Int,       // Số lỗi
    val hintCount: Int,        // Số gợi ý đã dùng
    val timestamp: Long = System.currentTimeMillis() // Thời điểm hoàn thành
)