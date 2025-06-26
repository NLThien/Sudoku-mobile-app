package com.example.sudokumobileapp.ui.screen

import android.content.Context
import android.util.Log
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
import androidx.compose.material3.Shapes
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.sudokumobileapp.domain.repository.TimerLifecycleObserver
import com.example.sudokumobileapp.ui.theme.SudokuMobileAppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore("settings")
private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")

@Composable
fun SudokuGameScreen(navController: NavController,modifier: Modifier,level: String) {
    val context = LocalContext.current
    var isDarkTheme by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    var difficulty by remember { mutableStateOf(level) } // chế độ game

    var selectedCell by remember { mutableStateOf<Pair<Int, Int>?>(null) } //gme

    var showExitDialog by remember { mutableStateOf(false) } // hôp thoại dialog khi nhấn thoát ra

    var elapsedTime by remember { mutableStateOf(0L) } // Thời gian tính bằng giây
    var isTimerRunning by remember { mutableStateOf(true) }

    var showPausedDialog by remember { mutableStateOf(false) }//hiện thị hộp toại khi nhấn tạm dừng

    val lifecycleOwner = LocalLifecycleOwner.current

    BackHandler(enabled = true) {
        showExitDialog = true
    }

    //lay theme tu datastore
    LaunchedEffect(Unit) {
        val preferences = context.dataStore.data.first()
        isDarkTheme = preferences[DARK_THEME_KEY] ?: false
    }

    // Bộ đếm thời gian
    LaunchedEffect(isTimerRunning) {
        if (isTimerRunning) {
            while (true) {
                delay(1000L) // Cập nhật mỗi giây
                elapsedTime++
                Log.d("TIMER_DEBUG", "Elapsed time: $elapsedTime seconds")
            }
        }
    }

    // Đăng ký lifecycle observer
    DisposableEffect(lifecycleOwner) {
        val observer = TimerLifecycleObserver(
            onPause = {
                isTimerRunning=false
                     },
        )
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

        if (showExitDialog) {
        isTimerRunning = false
        NotificationExit(
            onConfirm = {
                // Xử lý khi xác nhận thoát
                navController.popBackStack()
            },
            onDismiss = {
                showExitDialog = false
                isTimerRunning= true
            }
        )
    }
    // Hiển thị menu tạm dừng
    if (showPausedDialog) {
        PauseMenu(
            onResume = {
                showPausedDialog = false
                isTimerRunning= true
                       },
            onRestart = {
                // Xử lý chơi lại
                showPausedDialog = false
            },
            onExit = {
                navController.popBackStack() // Quay về màn hình trước
            }
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

    SudokuMobileAppTheme(darkTheme = isDarkTheme) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text(
                text = "Sudoku",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = if (isDarkTheme) Color.White else Color.Black,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.align(Alignment.Center)
            )

            Switch(
                checked = isDarkTheme,
                onCheckedChange = {
                    isDarkTheme = it
                    scope.launch {
                        context.dataStore.edit { settings ->
                            settings[DARK_THEME_KEY] = it
                        }
                    }
                },
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }



        // Chọn độ khó
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
                isTimerRunning=false
            },
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
            onCellSelected = { row, col -> selectedCell = row to col },
            isDarkTheme = isDarkTheme
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
            },
            isDarkTheme = isDarkTheme

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
                    color = if (isDarkTheme) Color.White else Color.Black,
                )
            )
        }
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
    onCellSelected: (Int, Int) -> Unit,
    isDarkTheme: Boolean
) {
    val cellBackgroundColor: (Int, Int, Boolean) -> Color = { row, col, isSelected ->
        when {
            isSelected -> if (isDarkTheme) Color.DarkGray else Color.LightGray
            (row / 3 + col / 3) % 2 == 0 -> if (isDarkTheme) Color(0xFF2C2C2C) else Color(0xFFE8F5E9)
            else -> if (isDarkTheme) Color(0xFF1E1E1E) else Color.White
        }
    }
    Column(
        modifier = Modifier
            .border(2.dp, if (isDarkTheme) Color.White else Color.Black)
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
                            .background(cellBackgroundColor(row, col, isSelected))
                            .clickable { onCellSelected(row, col) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (cellValue != 0) {
                            Text(
                                text = cellValue.toString(),
                                color = if (isDarkTheme) Color.White else if (isEditable) Color.Blue else Color.Black,
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
    onClearSelected: () -> Unit,
    isDarkTheme: Boolean
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
                NumberButton(number = number, onClick = { onNumberSelected(number) }, isDarkTheme = isDarkTheme)
            }
        }
        // Hàng số 4-6
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            for (number in 4..6) {
                NumberButton(number = number, onClick = { onNumberSelected(number) }, isDarkTheme = isDarkTheme)
            }
        }
        // Hàng số 7-9
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            for (number in 7..9) {
                NumberButton(number = number, onClick = { onNumberSelected(number) }, isDarkTheme = isDarkTheme)
            }
        }
        // Nút xóa
        Button(
            onClick = onClearSelected,
            modifier = Modifier.width(100.dp)
        ) {
            Text(
                text = "Xóa",
            )
        }
    }
}

@Composable
fun NumberButton(number: Int, onClick: () -> Unit, isDarkTheme: Boolean) {
    val backgroundColor = if (isDarkTheme) Color(0xFF2C2C2C) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val borderColor = if (isDarkTheme) Color.LightGray else Color.Gray

    Box(
        modifier = Modifier
            .size(48.dp)
            .background(backgroundColor)
            .border(1.dp, borderColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number.toString(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
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
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewSudokuGameScreen() {
    val navController = rememberNavController()
    SudokuGameScreen(
        navController = navController,
        modifier = Modifier,
        level = "Dễ"
    )
}
