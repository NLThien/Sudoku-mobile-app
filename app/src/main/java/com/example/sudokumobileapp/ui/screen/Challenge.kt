package com.example.sudokumobileapp.ui.screen

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.sudokumobileapp.R
import com.example.sudokumobileapp.data.repository.SoundManager
import com.example.sudokumobileapp.domain.model.Difficulty
import com.example.sudokumobileapp.domain.repository.TimerLifecycleObserver
import com.example.sudokumobileapp.domain.usecases.generator.BoardGenerator
import com.example.sudokumobileapp.domain.usecases.solver.SudokuSolver
import com.example.sudokumobileapp.domain.usecases.validator.BoardValidator
import com.example.sudokumobileapp.ui.theme.SudokuMobileAppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.example.sudokumobileapp.data.repository.ThemePreferences

//// DataStore
//private val Context.dataStore by preferencesDataStore("settings")
//private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")

// Format thời gian
fun countDownTime(seconds: Long): String {
    val safeSeconds = if (seconds < 0) 0 else seconds
    val minutes = safeSeconds / 60
    val remainingSeconds = safeSeconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}


// Kiểm tra ô có cho phép chỉnh sửa không
private fun isEditableCell(initialBoard: Array<Array<MutableState<Int>>>, row: Int, col: Int): Boolean {
    return initialBoard[row][col].value == 0
}

@Composable
fun SudokuChallengeGameScreen(
    navController: NavController,
    modifier: Modifier,
    level: String,
    mode: String
) {
    val context = LocalContext.current
    var isDarkTheme by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    var selectedCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var showExitDialog by remember { mutableStateOf(false) }
    var showWinDialog by remember { mutableStateOf(false) }
    var showGameOverDialog by remember { mutableStateOf(false) }
    var isTimerRunning by remember { mutableStateOf(true) }
    val lifecycleOwner = LocalLifecycleOwner.current

    val generator = remember { BoardGenerator() }
    val solver = remember { SudokuSolver() }
    val validator = remember { BoardValidator() }

    val diffEnum = when (level) {
        stringResource(R.string.difficulty_easy) -> Difficulty.EASY
        stringResource(R.string.difficulty_medium) -> Difficulty.MEDIUM
        stringResource(R.string.difficulty_hard) -> Difficulty.HARD
        else -> Difficulty.EASY
    }

    val challengeTimeLimit = when (diffEnum) {
        Difficulty.EASY -> 180L
        Difficulty.MEDIUM -> 360L
        Difficulty.HARD -> 600L
    }

    var remainingTime by remember { mutableStateOf(challengeTimeLimit) }

    var rawBoard = remember(diffEnum) { generator.generate(diffEnum).cells }
    var board = remember {
        mutableStateOf(Array(9) { row -> Array(9) { col -> mutableStateOf(rawBoard[row][col]) } })
    }
    val initialBoard = remember {
        board.value.map { row ->
            row.map { cell -> mutableStateOf(cell.value) }.toTypedArray()
        }.toTypedArray()
    }
    val solution = remember { solver.solve(rawBoard) ?: Array(9) { IntArray(9) } }

    // Xử lý nút back
    BackHandler(enabled = true) { showExitDialog = true }

    // Đọc dark theme từ DataStore
    LaunchedEffect(Unit) {
        isDarkTheme = ThemePreferences.loadTheme(context)
    }

    // Timer đếm ngược
    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning && remainingTime > 0) {
            delay(1000L)
            remainingTime--
            if (remainingTime <= 0) {
                remainingTime = 0
                isTimerRunning = false
                showGameOverDialog = true
            }
        }
    }

    // Observe Lifecycle (pause/resume)
    DisposableEffect(lifecycleOwner) {
        val observer = TimerLifecycleObserver(
            onPause = { isTimerRunning = false },
            onResume = { isTimerRunning = true }
        )
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Dialogs
    if (showExitDialog) NotificationExit(
        onConfirm = { navController.popBackStack() },
        onDismiss = { showExitDialog = false; isTimerRunning = true }
    )

    if (showWinDialog) SudokuWinDialog(
        time = challengeTimeLimit - remainingTime,
        onRestart = {
            rawBoard = generator.generate(diffEnum).cells
            board.value = Array(9) { row -> Array(9) { col -> mutableStateOf(rawBoard[row][col]) } }
            remainingTime = challengeTimeLimit
            isTimerRunning = true
            showWinDialog = false
        },
        onExit = { navController.popBackStack() }
    )

    if (showGameOverDialog) {
        Dialog(
            onDismissRequest = {
            }, properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                decorFitsSystemWindows = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0f)), // Nền mờ
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .width(320.dp),
                    shape = RoundedCornerShape(16.dp),
                    tonalElevation = 8.dp,
                    shadowElevation = 16.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.time_up),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(R.string.you_ran_out_of_time),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = {
                                    SoundManager.playClickSound()
                                    navController.popBackStack()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(text = stringResource(id = R.string.exit))
                            }

                            Button(
                                onClick = {
                                    SoundManager.playClickSound()
                                    rawBoard = generator.generate(diffEnum).cells
                                    board.value = Array(9) { row -> Array(9) { col -> mutableStateOf(rawBoard[row][col]) } }
                                    remainingTime = challengeTimeLimit
                                    isTimerRunning = true
                                    showGameOverDialog = false
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(text = stringResource(id = R.string.restart))
                            }
                        }
                    }
                }
            }
        }
    }


    // UI Game Screen
    SudokuMobileAppTheme(darkTheme = isDarkTheme) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = if (isDarkTheme) Color.White else Color.Black,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.align(Alignment.Center)
                )

                Text(
                    text = countDownTime(remainingTime),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = if (isDarkTheme) Color.White else Color.Black,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.align(Alignment.TopStart)
                )

                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = {
                        isDarkTheme = it
                        scope.launch {
                            ThemePreferences.saveTheme(context, it)
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }

            // Difficulty Display
            Button(onClick = {}, modifier = Modifier.padding(bottom = 16.dp)) {
                Text("${stringResource(R.string.difficulty)}: $level")
            }

            // Sudoku Grid
            Column(
                modifier = Modifier.border(2.dp, if (isDarkTheme) Color.White else Color.Black)
            ) {
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
                                modifier = Modifier
                                    .size(36.dp)
                                    .border(1.dp, Color.Gray)
                                    .background(cellColor)
                                    .clickable { selectedCell = row to col },
                                contentAlignment = Alignment.Center
                            ) {
                                if (cell.value != 0) {
                                    Text(
                                        text = cell.value.toString(),
                                        color = if (solution[row][col] == 0) Color.Black
                                        else if (isCorrect) Color.Black else Color.Red,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Number Pad
            Column(
                modifier = Modifier.padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (chunk in listOf(1..3, 4..6, 7..9)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        for (number in chunk) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(if (isDarkTheme) Color(0xFF2C2C2C) else Color.White)
                                    .border(1.dp, if (isDarkTheme) Color.LightGray else Color.Gray)
                                    .clickable {
                                        selectedCell?.let { (row, col) ->
                                            if (isEditableCell(initialBoard, row, col)) {
                                                board.value[row][col].value = number
                                                if (validator.isBoardValid(board.value.map { row ->
                                                        row.map { it.value }.toIntArray()
                                                    }.toTypedArray())) {
                                                    isTimerRunning = false
                                                    showWinDialog = true
                                                }
                                            }
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    number.toString(),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkTheme) Color.White else Color.Black
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        SoundManager.playClickSound()
                        selectedCell?.let { (row, col) ->
                            if (isEditableCell(initialBoard, row, col)) {
                                board.value[row][col].value = 0
                            }
                        }
                    },
                    modifier = Modifier.width(100.dp)
                ) {
                    Text(stringResource(id = R.string.remove))
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewSudokuChallengeGameScreen() {
    val navController = rememberNavController()
    SudokuChallengeGameScreen(
        navController = navController,
        modifier = Modifier,
        level = stringResource(id = R.string.difficulty_easy),
        mode = "challenge"
    )
}