package com.example.sudokumobileapp.domain.usecases.generator

// tạo bẳng sudoku
import com.example.sudokumobileapp.domain.model.SudokuBoard
import com.example.sudokumobileapp.domain.model.Difficulty

class BoardGenerator {
    fun generate(difficulty: Difficulty): SudokuBoard {
        // Bước 1: Tạo bảng giải được
        val solvedBoard = generateSolvedBoard()

        // Bước 2: Xóa ô theo độ khó
        val puzzleBoard = removeCells(solvedBoard, difficulty.emptyCells)

        return SudokuBoard(
            cells = puzzleBoard,
            difficulty = difficulty
        )
    }

    private fun generateSolvedBoard(): Array<IntArray> {
        val board = Array(9) { IntArray(9) }
        solve(board) // Dùng backtracking
        return board
    }

    private fun solve(board: Array<IntArray>): Boolean {
        for (row in 0..8) {
            for (col in 0..8) {
                if (board[row][col] == 0) {
                    for (num in (1..9).shuffled()) {  //tạo tính ngẫu nhiên, tránh lặp lại theo thứ tự cũ
                        if (isValid(board, row, col, num)) {
                            board[row][col] = num
                            if (solve(board)) return true
                            board[row][col] = 0
                        }
                    }
                    return false
                }
            }
        }
        return true
    }

    private fun removeCells(board: Array<IntArray>, emptyCells: Int): Array<IntArray> {
        val puzzle = board.map { it.clone() }.toTypedArray()
        var cellsRemoved = 0

        while (cellsRemoved < emptyCells) {
            val row = (0..8).random()
            val col = (0..8).random()
            if (puzzle[row][col] != 0) {
                puzzle[row][col] = 0
                cellsRemoved++
            }
        }
        return puzzle
    }

    private fun isValid(board: Array<IntArray>, row: Int, col: Int, num: Int): Boolean {
        // Kiểm tra hàng
        for (i in 0..8) {
            if (board[row][i] == num) return false
        }

        // Kiểm tra cột
        for (i in 0..8) {
            if (board[i][col] == num) return false
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