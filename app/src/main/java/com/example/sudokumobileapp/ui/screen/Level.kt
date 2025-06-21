package com.example.sudokumobileapp.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun SudokuGameScreen(navController: NavController,modifier: Modifier,level: String) {
    var difficulty by remember { mutableStateOf(level) }
    var showDifficultyMenu by remember { mutableStateOf(false) }
    var selectedCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var showExitDialog by remember { mutableStateOf(false) }


    var elapsedTime by remember { mutableStateOf(0L) } // Thời gian tính bằng giây
    var isTimerRunning by remember { mutableStateOf(true) }


    BackHandler(enabled = true) {
        showExitDialog = true
    }

    // Bộ đếm thời gian
    LaunchedEffect(isTimerRunning) {
        if (isTimerRunning) {
            while (true) {
                delay(1000L) // Cập nhật mỗi giây
                elapsedTime++
            }
        }
    }

    if (showExitDialog) {
        NotificationExit(
            onConfirm = {
                // Xử lý khi xác nhận thoát
                navController.popBackStack()
            },
            onDismiss = { showExitDialog = false }
        )
    }
    // Tạo bảng Sudoku mẫu (0 đại diện cho ô trống)
    val board = remember(difficulty) {
        when (difficulty) {
            "Dễ" -> generateSudokuBoard(30) // Ít ô trống
            "Trung bình" -> generateSudokuBoard(45) // Trung bình ô trống
            "Khó" -> generateSudokuBoard(60) // Nhiều ô trống
            else -> generateSudokuBoard(30)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Tiêu đề
        Text(
            text = "Sudoku",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Chọn độ khó
        Box {
            Button(
                onClick = { showDifficultyMenu = true },
                modifier = Modifier.padding(bottom = 16.dp)

            ) {
                Text(text = "Độ khó: $difficulty")
            }
        }
        Button(
            onClick = {},
            modifier = Modifier.padding(bottom = 16.dp)

        ) {
            Text(
                text = "Tạm dừng"
            )
        }

        // Bảng Sudoku
        SudokuBoard(
            board = board,
            selectedCell = selectedCell,
            onCellSelected = { row, col -> selectedCell = row to col }
        )

        // Bàn phím số
        NumberPad(
            modifier = Modifier.padding(top = 16.dp),
            onNumberSelected = { number ->
                selectedCell?.let { (row, col) ->
                    // Chỉ cho phép điền số vào ô trống (giá trị 0)
                    if (board[row][col] == 0) {
                        board[row][col] = number
                    }
                }
            },
            onClearSelected = {
                selectedCell?.let { (row, col) ->
                    // Chỉ cho phép xóa ô không phải là ô cố định
                    if (isEditableCell(board, row, col)) {
                        board[row][col] = 0
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
    selectedCell: Pair<Int, Int>?,
    onCellSelected: (Int, Int) -> Unit
) {
    Column(
        modifier = Modifier
            .border(2.dp, Color.Black)
    ) {
        for (row in 0 until 9) {
            Row {
                for (col in 0 until 9) {
                    val cellValue = board[row][col]
                    val isSelected = selectedCell?.let { it.first == row && it.second == col } ?: false
                    val isEditable = isEditableCell(board, row, col)

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .border(1.dp, Color.Gray)
                            .background(
                                when {
                                    isSelected -> Color.LightGray
                                    row / 3 != (row + 1) / 3 && col / 3 != (col + 1) / 3 -> Color(0xFFE8F5E9)
                                    else -> Color.White
                                }
                            )
                            .clickable { onCellSelected(row, col) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (cellValue != 0) {
                            Text(
                                text = cellValue.toString(),
                                color = if (isEditable) Color.Blue else Color.Black,
                                fontSize = 20.sp,
                                fontWeight = if (isEditable) FontWeight.Normal else FontWeight.Bold
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

// Hàm kiểm tra ô có thể chỉnh sửa không (ô trống ban đầu)
private fun isEditableCell(board: Array<IntArray>, row: Int, col: Int): Boolean {
    // Trong thực tế, cần kiểm tra xem ô này có phải là ô gốc không
    // Ở đây đơn giản coi tất cả ô 0 là có thể chỉnh sửa
    return board[row][col] == 0
}

// Hàm tạo bảng Sudoku (đơn giản)
private fun generateSudokuBoard(emptyCells: Int): Array<IntArray> {
    // Trong thực tế, bạn cần một thuật toán tạo Sudoku hợp lệ
    // Đây chỉ là ví dụ đơn giản

    val board = Array(9) { IntArray(9) { 0 } }

    // Điền một số ô ngẫu nhiên
    repeat(81 - emptyCells) {
        val row = (0 until 9).random()
        val col = (0 until 9).random()
        val num = (1..9).random()

        if (board[row][col] == 0 && isValidPlacement(board, row, col, num)) {
            board[row][col] = num
        }
    }

    return board
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

