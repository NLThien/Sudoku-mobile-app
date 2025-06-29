package com.example.sudokumobileapp.ui.screen

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.sudokumobileapp.domain.repository.TimerLifecycleObserver
import kotlinx.coroutines.delay


@Composable
fun SudokuGameScreen(navController: NavController, modifier: Modifier, level: String) {
    var difficulty by remember { mutableStateOf(level) }
    var selectedCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var showExitDialog by remember { mutableStateOf(false) }
    var elapsedTime by remember { mutableStateOf(0L) }
    var isTimerRunning by remember { mutableStateOf(true) }
    var showPausedDialog by remember { mutableStateOf(false) }
    var errorCount by remember { mutableStateOf(0) }
    var isGameWon by remember { mutableStateOf(false) }
    var isGameLost by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorDialogTime by remember { mutableStateOf(0L) }
    val lifecycleOwner = LocalLifecycleOwner.current

    val (initialBoard, solutionBoard) = remember(difficulty) {
        generateValidSudokuBoard(when (difficulty) {
            "Dễ" -> 30
            "Trung bình" -> 45
            "Khó" -> 60
            else -> 30
        })
    }

    var currentBoard by remember { mutableStateOf(initialBoard.map { it.clone() }.toTypedArray()) }

    BackHandler(enabled = true) {
        showExitDialog = true
    }

    LaunchedEffect(isTimerRunning) {
        if (isTimerRunning) {
            while (true) {
                delay(1000L)
                elapsedTime++
                Log.d("TIMER_DEBUG", "Elapsed time: $elapsedTime seconds")
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = TimerLifecycleObserver(
            onPause = { isTimerRunning = false },
        )
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (showExitDialog) {
        isTimerRunning = false
        NotificationExit(
            onConfirm = { navController.popBackStack() },
            onDismiss = {
                showExitDialog = false
                isTimerRunning = true
            }
        )
    }

    if (showPausedDialog) {
        PauseMenu(
            onResume = {
                showPausedDialog = false
                isTimerRunning = true
            },
            onRestart = {
                // Reset game state
                currentBoard = initialBoard.map { it.clone() }.toTypedArray()
                errorCount = 0
                elapsedTime = 0L
                isGameWon = false
                isGameLost = false
                showPausedDialog = false
                isTimerRunning = true
            },
            onExit = { navController.popBackStack() }
        )
    }

    if (isGameWon) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Chúc mừng!") },
            text = { Text("Bạn đã hoàn thành Sudoku!\nThời gian: ${formatTime(elapsedTime)}") },
            confirmButton = {
                Button(onClick = { navController.popBackStack() }) {
                    Text("OK")
                }
            }
        )
    }

    if (isGameLost) {
        AlertDialog(
            onDismissRequest = { navController.popBackStack() },
            title = { Text("Game Over") },
            text = { Text("Bạn đã sai quá 3 lần!\nHãy thử lại nhé!") },
            confirmButton = {
                Button(onClick = { navController.popBackStack() }) {
                    Text("OK")
                }
            }
        )
    }


    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Sai rồi!") },
            text = {
                Column {
                    Text("Số bạn điền không đúng!")
                    Text("Thời gian: ${formatTime(errorDialogTime)}")
                }
            },
            confirmButton = {
                Button(onClick = { showErrorDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sudoku",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Box {
            Button(
                onClick = { },
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(text = "Độ khó: $difficulty")
            }
        }

        Button(
            onClick = {
                showPausedDialog = true
                isTimerRunning = false
            },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(text = "Tạm dừng")
        }

        Text(
            text = "Sai: $errorCount/3",
            color = if (errorCount >= 2) Color.Red else Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        SudokuBoard(
            board = currentBoard,
            initialBoard = initialBoard,
            selectedCell = selectedCell,
            onCellSelected = { row, col -> selectedCell = row to col }
        )

        NumberPad(
            modifier = Modifier.padding(top = 16.dp),
            onNumberSelected = { number ->
                selectedCell?.let { (row, col) ->
                    // Chỉ cho phép điền vào ô trống ban đầu
                    if (initialBoard[row][col] == 0) {
                        val newBoard = currentBoard.map { it.clone() }.toTypedArray()
                        newBoard[row][col] = number

                        // Kiểm tra tính hợp lệ
                        if (number != solutionBoard[row][col]) {
                            errorCount++
                            errorDialogTime = elapsedTime
                            showErrorDialog = true

                            if (errorCount >= 3) {
                                isGameLost = true
                                isTimerRunning = false
                            }
                        }

                        currentBoard = newBoard

                        // Kiểm tra hoàn thành
                        if (isBoardComplete(currentBoard)) {
                            isGameWon = true
                            isTimerRunning = false
                        }
                    }
                }
            },
            onClearSelected = {
                selectedCell?.let { (row, col) ->
                    // Chỉ xóa ô có thể chỉnh sửa
                    if (initialBoard[row][col] == 0) {
                        val newBoard = currentBoard.map { it.clone() }.toTypedArray()
                        newBoard[row][col] = 0
                        currentBoard = newBoard
                    }
                }
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formatTime(elapsedTime),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                )
            )
        }
    }
}

// Định dạng thời gian
fun formatTime(seconds: Long): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

@Composable
fun SudokuBoard(
    board: Array<IntArray>,
    initialBoard: Array<IntArray>,
    selectedCell: Pair<Int, Int>?,
    onCellSelected: (Int, Int) -> Unit
) {
    Column(modifier = Modifier.border(2.dp, Color.Black)) {
        for (row in 0 until 9) {
            Row {
                for (col in 0 until 9) {
                    val cellValue = board[row][col]
                    val isSelected = selectedCell?.let { it.first == row && it.second == col } ?: false
                    val isInitial = initialBoard[row][col] != 0 // Ô ban đầu không thể chỉnh sửa

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .border(1.dp, Color.Gray)
                            .background(
                                when {
                                    isSelected -> Color.LightGray
                                    isInitial -> Color.LightGray.copy(alpha = 0.3f)
                                    row / 3 != (row + 1) / 3 && col / 3 != (col + 1) / 3 -> Color(0xFFE8F5E9)
                                    else -> Color.White
                                }
                            )
                            .clickable(enabled = !isInitial) { onCellSelected(row, col) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (cellValue != 0) {
                            Text(
                                text = cellValue.toString(),
                                color = if (isInitial) Color.Black else Color.Blue,
                                fontSize = 20.sp,
                                fontWeight = if (isInitial) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NumberPad(
    modifier: Modifier = Modifier,
    onNumberSelected: (Int) -> Unit,
    onClearSelected: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hàng số 1-3
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            for (number in 1..3) {
                NumberButton(number = number, onClick = { onNumberSelected(number) })
            }
        }
        // Hàng số 4-6
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            for (number in 4..6) {
                NumberButton(number = number, onClick = { onNumberSelected(number) })
            }
        }
        // Hàng số 7-9
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            for (number in 7..9) {
                NumberButton(number = number, onClick = { onNumberSelected(number) })
            }
        }
        // Nút xóa
        Button(
            onClick = onClearSelected,
            modifier = Modifier.width(100.dp)
        ) {
            Text("Xóa")
        }

    }
}

@Composable
fun NumberButton(number: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(Color.White)
            .border(1.dp, Color.Gray)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number.toString(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


// Hàm tạo bảng Sudoku hợp lệ có thể giải được
private fun generateValidSudokuBoard(emptyCells: Int): Pair<Array<IntArray>, Array<IntArray>> {
    // Tạo bảng giải đầy đủ
    val solution = generateFullSudokuBoard()

    // Tạo bảng chơi bằng cách xóa một số ô
    val board = solution.map { it.clone() }.toTypedArray()
    repeat(emptyCells) {
        var row: Int
        var col: Int
        do {
            row = (0 until 9).random()
            col = (0 until 9).random()
        } while (board[row][col] == 0)

        board[row][col] = 0
    }

    return Pair(board, solution)
}

// Hàm tạo bảng Sudoku đầy đủ hợp lệ
private fun generateFullSudokuBoard(): Array<IntArray> {
    val board = Array(9) { IntArray(9) { 0 } }

    // Điền các số từ 1-9 vào đường chéo chính các ô 3x3
    for (box in 0 until 9 step 3) {
        fillDiagonalBox(board, box, box)
    }

    // Giải phần còn lại
    solveSudoku(board)

    return board
}

// Điền số vào ô 3x3 trên đường chéo
private fun fillDiagonalBox(board: Array<IntArray>, row: Int, col: Int) {
    val nums = (1..9).shuffled()
    var index = 0
    for (i in 0 until 3) {
        for (j in 0 until 3) {
            board[row + i][col + j] = nums[index++]
        }
    }
}

// Hàm giải Sudoku (sử dụng backtracking)
private fun solveSudoku(board: Array<IntArray>): Boolean {
    for (row in 0 until 9) {
        for (col in 0 until 9) {
            if (board[row][col] == 0) {
                for (num in 1..9) {
                    if (isValidPlacement(board, row, col, num)) {
                        board[row][col] = num
                        if (solveSudoku(board)) {
                            return true
                        }
                        board[row][col] = 0
                    }
                }
                return false
            }
        }
    }
    return true
}


// Kiểm tra vị trí đặt số có hợp lệ không
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
    for (i in 0 until 3) {
        for (j in 0 until 3) {
            if (board[boxRow + i][boxCol + j] == num) return false
        }
    }

    return true
}

private fun isBoardComplete(board: Array<IntArray>): Boolean {
    // Kiểm tra tất cả ô đã được điền
    for (row in 0 until 9) {
        for (col in 0 until 9) {
            if (board[row][col] == 0) return false
        }
    }

    // Kiểm tra tính hợp lệ của toàn bộ bảng
    for (row in 0 until 9) {
        for (col in 0 until 9) {
            val num = board[row][col]
            board[row][col] = 0 // Tạm thời xóa để kiểm tra
            if (!isValidPlacement(board, row, col, num)) {
                board[row][col] = num // Khôi phục giá trị
                return false
            }
            board[row][col] = num // Khôi phục giá trị
        }
    }
    return true
}