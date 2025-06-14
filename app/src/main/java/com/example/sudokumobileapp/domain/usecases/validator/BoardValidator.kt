package com.example.sudokumobileapp.domain.usecases.validator

//kiểm tra

class BoardValidator {

    fun isBoardValid(board: Array<IntArray>): Boolean {
        // Kiểm tra tất cả ô đã được điền
        if (board.any { row -> row.any { it == 0 } }) return false

        // Kiểm tra từng hàng/cột/ô 3x3
        return (0..8).all { index ->
            isValidRow(board, index) &&
                    isValidColumn(board, index) &&
                    isValidBox(board, index)
        }
    }

    fun isValidMove(board: Array<IntArray>, row: Int, col: Int, num: Int): Boolean {
        // Kiểm tra khi người chơi điền số
        if (board[row][col] != 0) return false

        val tempBoard = board.map { it.clone() }.toTypedArray()
        tempBoard[row][col] = num
        return isValidRow(tempBoard, row) &&
                isValidColumn(tempBoard, col) &&
                isValidBox(tempBoard, getBoxIndex(row, col))
    }

    private fun isValidRow(board: Array<IntArray>, row: Int): Boolean {
        val numbers = board[row].filter { it != 0 }
        return numbers.size == numbers.distinct().size
    }

    private fun isValidColumn(board: Array<IntArray>, col: Int): Boolean {
        val numbers = (0..8).map { board[it][col] }.filter { it != 0 }
        return numbers.size == numbers.distinct().size
    }

    private fun isValidBox(board: Array<IntArray>, boxIndex: Int): Boolean {
        val startRow = (boxIndex / 3) * 3
        val startCol = (boxIndex % 3) * 3
        val numbers = mutableListOf<Int>()

        for (i in 0..2) {
            for (j in 0..2) {
                val num = board[startRow + i][startCol + j]
                if (num != 0) numbers.add(num)
            }
        }
        return numbers.size == numbers.distinct().size
    }

    private fun getBoxIndex(row: Int, col: Int): Int = (row / 3) * 3 + (col / 3)
}