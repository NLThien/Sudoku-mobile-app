package com.example.sudokumobileapp.ui.screen

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.sudokumobileapp.domain.model.Difficulty
import com.example.sudokumobileapp.domain.repository.TimerLifecycleObserver
import com.example.sudokumobileapp.domain.usecases.generator.BoardGenerator
import com.example.sudokumobileapp.domain.usecases.solver.SudokuSolver
import com.example.sudokumobileapp.domain.usecases.validator.BoardValidator
import com.example.sudokumobileapp.ui.theme.SudokuMobileAppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// Tạo DataStore để lưu chế độ theme
private val Context.dataStore by preferencesDataStore("settings")
private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")

// Hàm format thời gian hiển thị
fun formatTime(seconds: Long): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

// Hàm kiểm tra ô có phải là ô được phép chỉnh sửa hay không (ô ban đầu trống)
private fun isEditableCell(initialBoard: Array<Array<MutableState<Int>>>, row: Int, col: Int): Boolean {
    return initialBoard[row][col].value == 0
}

// Composable màn hình chơi Sudoku
@Composable
fun SudokuGameScreen(navController: NavController, modifier: Modifier, level: String) {
    val context = LocalContext.current
    var isDarkTheme by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Các trạng thái game
    var selectedCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var showExitDialog by remember { mutableStateOf(false) }
    var showPausedDialog by remember { mutableStateOf(false) }
    var showWinDialog by remember { mutableStateOf(false) }
    var elapsedTime by remember { mutableStateOf(0L) }
    var isTimerRunning by remember { mutableStateOf(true) }
    val lifecycleOwner = LocalLifecycleOwner.current

    val generator = remember { BoardGenerator() }
    val solver = remember { SudokuSolver() }
    val validator = remember { BoardValidator() }

    // Chuyển độ khó từ chuỗi sang enum
    val diffEnum = when (level) {
        "Dễ" -> Difficulty.EASY
        "Trung bình" -> Difficulty.MEDIUM
        "Khó" -> Difficulty.HARD
        else -> Difficulty.EASY
    }

    // Tạo bảng game và lời giải
    var rawBoard = remember(diffEnum) { generator.generate(diffEnum).cells }
    var board = remember { mutableStateOf(Array(9) { row -> Array(9) { col -> mutableStateOf(rawBoard[row][col]) } }) }
    val initialBoard = remember { board.value.map { row -> row.map { cell -> mutableStateOf(cell.value) }.toTypedArray() }.toTypedArray() }
    val solution = remember { solver.solve(rawBoard) ?: Array(9) { IntArray(9) } }

    // BackHandler khi nhấn nút quay lại
    BackHandler(enabled = true) { showExitDialog = true }

    // Tải theme từ DataStore
    LaunchedEffect(Unit) {
        val preferences = context.dataStore.data.first()
        isDarkTheme = preferences[DARK_THEME_KEY] ?: false
    }

    // Đếm thời gian
    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning) {
            delay(1000L)
            elapsedTime++
        }
    }

    // Lắng nghe lifecycle để tạm dừng timer
    DisposableEffect(lifecycleOwner) {
        val observer = TimerLifecycleObserver(onPause = { isTimerRunning = false }, onResume = {isTimerRunning = true})
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Hiển thị các hộp thoại
    if (showExitDialog) NotificationExit(
        onConfirm = { navController.popBackStack() },
        onDismiss = { showExitDialog = false; isTimerRunning = true }
    )

    if (showPausedDialog) PauseMenu(
        onResume = { showPausedDialog = false; isTimerRunning = true },
        onRestart = {},
        onExit = { navController.popBackStack() }
    )

    if (showWinDialog) SudokuWinDialog(
        time = elapsedTime,
        onRestart = {
            rawBoard = generator.generate(diffEnum).cells
            board.value = Array(9) { row -> Array(9) { col -> mutableStateOf(rawBoard[row][col]) } }
            elapsedTime = 0L
            isTimerRunning = true
            showWinDialog = false
        },
        onExit = { navController.popBackStack() }
    )

    // Theme toàn màn
    SudokuMobileAppTheme(darkTheme = isDarkTheme) {
        Column(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header chứa tiêu đề, thời gian và switch theme
            Box(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                Text("Sudoku", style = MaterialTheme.typography.headlineLarge.copy(
                    color = if (isDarkTheme) Color.White else Color.Black,
                    fontWeight = FontWeight.Bold
                ), modifier = Modifier.align(Alignment.Center))

                Text(formatTime(elapsedTime), style = MaterialTheme.typography.headlineLarge.copy(
                    color = if (isDarkTheme) Color.White else Color.Black,
                    fontWeight = FontWeight.Bold
                ), modifier = Modifier.align(Alignment.TopStart))

                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = {
                        isDarkTheme = it
                        scope.launch {
                            context.dataStore.edit { settings -> settings[DARK_THEME_KEY] = it }
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }

            // Nút chọn độ khó
            Button(onClick = {}, modifier = Modifier.padding(bottom = 16.dp)) {
                Text("Độ khó: $level")
            }

            // Nút tạm dừng và gợi ý
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = { showPausedDialog = true; isTimerRunning = false },
                    modifier = Modifier.padding(end = 8.dp)) { Text("Tạm dừng") }

                Button(
                    onClick = {
                        selectedCell?.let { (r, c) ->
                            if (board.value[r][c].value == 0) {
                                board.value[r][c].value = solution[r][c]
                                if (validator.isBoardValid(board.value.map { row -> row.map { it.value }.toIntArray() }.toTypedArray())) {
                                    isTimerRunning = false
                                    showWinDialog = true
                                }
                            }
                        }
                    },
                    enabled = selectedCell?.let { board.value[it.first][it.second].value == 0 } == true,
                    modifier = Modifier.width(120.dp)
                ) { Text("Gợi ý") }
            }

            // Bảng Sudoku
            Column(modifier = Modifier.border(2.dp, if (isDarkTheme) Color.White else Color.Black)) {
                for (row in 0 until 9) {
                    Row {
                        for (col in 0 until 9) {
                            val cell = board.value[row][col]
                            val isSelected = selectedCell?.let { it.first == row && it.second == col } ?: false
                            val isCorrect = solution[row][col] == cell.value
                            val cellColor = when {
                                isSelected -> if (isDarkTheme) Color.DarkGray else Color.LightGray
                                (row / 3 + col / 3) % 2 == 0 -> if (isDarkTheme) Color(0xFF2C2C2C) else Color(0xFFE8F5E9)
                                else -> if (isDarkTheme) Color(0xFF1E1E1E) else Color.White
                            }

                            Box(
                                modifier = Modifier.size(36.dp).border(1.dp, Color.Gray).background(cellColor).clickable {
                                    selectedCell = row to col
                                },
                                contentAlignment = Alignment.Center
                            ) {
                                if (cell.value != 0) {
                                    Text(
                                        text = cell.value.toString(),
                                        color = if (solution[row][col] == 0) Color.Black else if (isCorrect) Color.Black else Color.Red,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bàn phím số
            Column(modifier = Modifier.padding(top = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                for (chunk in listOf(1..3, 4..6, 7..9)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 8.dp)) {
                        for (number in chunk) {
                            Box(
                                modifier = Modifier.size(48.dp).background(if (isDarkTheme) Color(0xFF2C2C2C) else Color.White)
                                    .border(1.dp, if (isDarkTheme) Color.LightGray else Color.Gray).clickable {
                                        selectedCell?.let { (row, col) ->
                                            if (isEditableCell(initialBoard, row, col)) {
                                                board.value[row][col].value = number
                                                if (validator.isBoardValid(board.value.map { row -> row.map { it.value }.toIntArray() }.toTypedArray())) {
                                                    isTimerRunning = false
                                                    showWinDialog = true
                                                }
                                            }
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(number.toString(), fontSize = 24.sp, fontWeight = FontWeight.Bold,
                                    color = if (isDarkTheme) Color.White else Color.Black)
                            }
                        }
                    }
                }

                // Nút xóa
                Button(onClick = {
                    selectedCell?.let { (row, col) ->
                        if (isEditableCell(initialBoard, row, col)) {
                            board.value[row][col].value = 0
                        }
                    }
                }, modifier = Modifier.width(100.dp)) {
                    Text("Xóa")
                }
            }
        }
    }
}

// Xem thử màn hình
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
