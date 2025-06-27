package com.example.sudokumobileapp.data.repository

// quản lý dữ liệu cục bộ trong ứng dụng(khởi tạo, lưu,

import androidx.room.withTransaction
import com.example.sudokumobileapp.data.local.SudokuDatabase
import com.example.sudokumobileapp.domain.model.Difficulty
import com.example.sudokumobileapp.domain.model.SudokuBoard
import com.example.sudokumobileapp.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import com.example.sudokumobileapp.data.mapper.SudokuMapper

class GameRepositoryImpl @Inject constructor(
    private val database: SudokuDatabase,    // lúc này chưa tạo
    private val mapper: SudokuMapper // Thêm mapper
) : GameRepository {

    private val gameDao = database.gameDao()

    override suspend fun saveGame(board: SudokuBoard) {
        database.withTransaction {
            // Xóa game cũ nếu có
            gameDao.deleteCurrentGame()
            // Lưu game mới
            gameDao.insertGame(mapper.toEntity(board))
        }
    }

    override fun loadGame(): Flow<SudokuBoard?> =
        gameDao.observeCurrentGame().map { entity ->
            entity?.let { mapper.toDomain(it) }
        }

    override suspend fun generateNewBoard(difficulty: Difficulty): SudokuBoard {
        val newBoard = when (difficulty) {
            Difficulty.EASY -> generateBoardWithEmptyCells(40)
            Difficulty.MEDIUM -> generateBoardWithEmptyCells(30)
            Difficulty.HARD -> generateBoardWithEmptyCells(25)
        }
        saveGame(newBoard)
        return newBoard
    }

    override suspend fun validateBoard(board: SudokuBoard): Boolean {
        return isBoardValid(board.cells)
    }

    // Các phương thức hỗ trợ
    private fun generateBoardWithEmptyCells(cellsToKeep: Int): SudokuBoard {
        // Triển khai logic sinh bảng
        // Bước 1: Tạo bảng giải được hoàn chỉnh
        val solvedBoard = generateSolvedBoard()

        // Bước 2: Xóa ngẫu nhiên các ô
        val puzzleBoard = removeCells(solvedBoard, 81 - cellsToKeep)

        return SudokuBoard(
            cells = puzzleBoard,
            difficulty = when (cellsToKeep) {
                40 -> Difficulty.EASY
                30 -> Difficulty.MEDIUM
                else -> Difficulty.HARD
            }
        )
    }

    private fun generateSolvedBoard(): Array<IntArray> {
        val board = Array(9) { IntArray(9) }
        solveSudoku(board)
        return board
    }

    private fun solveSudoku(board: Array<IntArray>): Boolean {
        for (row in 0 until 9) {
            for (col in 0 until 9) {
                if (board[row][col] == 0) {
                    for (num in 1..9) {
                        if (isValidPlacement(board, row, col, num)) {
                            board[row][col] = num
                            if (solveSudoku(board)) return true
                            board[row][col] = 0
                        }
                    }
                    return false
                }
            }
        }
        return true
    }

    private fun removeCells(board: Array<IntArray>, cellsToRemove: Int): Array<IntArray> {
        val puzzle = board.map { it.clone() }.toTypedArray()
        var removed = 0

        while (removed < cellsToRemove) {
            val row = (0 until 9).random()
            val col = (0 until 9).random()
            if (puzzle[row][col] != 0) {
                puzzle[row][col] = 0
                removed++
            }
        }
        return puzzle
    }

    private fun isBoardValid(board: Array<IntArray>): Boolean {
        // Kiểm tra tất cả ô đã điền
        if (board.any { row -> row.any { it == 0 } }) return false

        // Kiểm tra từng hàng, cột và ô 3x3
        return (0 until 9).all { index ->
            isValidRow(board, index) &&
                    isValidColumn(board, index) &&
                    isValidBox(board, index)
        }
    }

    private fun isValidPlacement(board: Array<IntArray>, row: Int, col: Int, num: Int): Boolean {
        // Kiểm tra hàng
        for (i in 0 until 9) {
            if (board[row][i] == num) return false
        }

        // Kiểm tra cột
        for (i in 0 until 9) {
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

    private fun isValidRow(board: Array<IntArray>, row: Int): Boolean {
        val numbers = board[row].filter { it != 0 }
        return numbers.size == numbers.toSet().size
    }

    private fun isValidColumn(board: Array<IntArray>, col: Int): Boolean {
        val numbers = (0 until 9).map { board[it][col] }.filter { it != 0 }
        return numbers.size == numbers.toSet().size
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
        return numbers.size == numbers.toSet().size
    }
}