package com.example.sudokumobileapp.domain.usecases.solver

// xử lý logic

class SudokuSolver {
// gọi backtracking cho bảng
    fun solve(board: Array<IntArray>): Array<IntArray>? {
        val solution = board.map { it.clone() }.toTypedArray()
        return if (backtrack(solution)) solution else null
    }
// thuật toán backtracking tìm lời giải
    private fun backtrack(board: Array<IntArray>): Boolean {
        for (row in 0..8) {
            for (col in 0..8) {
                if (board[row][col] == 0) {
                    for (num in 1..9) {
                        if (isValid(board, row, col, num)) {
                            board[row][col] = num
                            if (backtrack(board)) return true
                            board[row][col] = 0
                        }
                    }
                    return false
                }
            }
        }
        return true
    }

    private fun isValid(board: Array<IntArray>, row: Int, col: Int, num: Int): Boolean {
        // Kiểm tra hàng và cột
        for (i in 0..8) {
            if (board[row][i] == num || board[i][col] == num) return false
        }

        // Kiểm tra ô 3x3
        val boxRow = row - row % 3
        val boxCol = col - col % 3
        for (i in 0..2) {
            for (j in 0..2) {
                if (board[boxRow + i][boxCol + j] == num) return false
            }
        }
        return true
    }
}