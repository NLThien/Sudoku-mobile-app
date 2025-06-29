package com.example.sudokumobileapp.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SentimentNeutral
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class SudokuBoard(
    val cells: Array<IntArray>, // Lưới 9x9 (0 = ô trống)
    val difficulty: Difficulty, // Easy/Medium/Hard
    val isComplete: Boolean = false // kiểm tra đã xong game chưa
) {
    // Hàm copy để thay đổi giá trị ô
    fun updateCell(row: Int, col: Int, value: Int): SudokuBoard {
        val newCells = cells.map { it.clone() }.toTypedArray()
        newCells[row][col] = value
        return copy(cells = newCells)
    }
}

enum class Difficulty(
    val displayName: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val emptyCells: Int
) {
    EASY(
        "Dễ",
        "Cho người mới bắt đầu",
        Icons.Default.SentimentVerySatisfied,
        Color(0xFF4CAF50),
        30
    ),
    MEDIUM(
        "Trung bình",
        "Thử thách vừa phải",
        Icons.Default.SentimentNeutral,
        Color(0xFF2196F3),
        45
    ),
    HARD(
        "Khó",
        "Dành cho cao thủ",
        Icons.Default.SentimentVeryDissatisfied,
        Color(0xFFF44336),
        60
    )
}