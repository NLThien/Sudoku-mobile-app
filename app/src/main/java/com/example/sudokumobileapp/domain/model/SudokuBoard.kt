package com.example.sudokumobileapp.domain.model

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

enum class Difficulty(val emptyCells: Int) {
    EASY(40),   // giữ 41 ô ( tổng 81 ô tất cả)
    MEDIUM(50), // giữ lại 31 ô
    HARD(60)    // giữ lại 21 ô
}