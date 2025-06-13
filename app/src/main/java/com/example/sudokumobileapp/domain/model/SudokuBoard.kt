package com.example.sudokumobileapp.domain.model

data class SudokuBoard(
    val cells: Array<IntArray>, // Lưới 9x9 (0 = ô trống)
    val difficulty: Difficulty, // Easy/Medium/Hard
    val elapsedTime: Long = 0L  // Thời gian chơi (ms)
)

enum class Difficulty {
    EASY, MEDIUM, HARD
}