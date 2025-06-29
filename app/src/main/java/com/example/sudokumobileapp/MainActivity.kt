package com.example.sudokumobileapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sudokumobileapp.ui.screen.SudokuGameScreen
import com.example.sudokumobileapp.ui.theme.SudokuMobileAppTheme
import com.example.sudokumobileapp.ui.screen.SudokuChallengeGameScreen
import com.example.sudokumobileapp.data.repository.SoundManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SoundManager.init(this)
        enableEdgeToEdge()
        setContent {
            SudokuMobileAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SudokuApp(Modifier.padding(innerPadding))
                }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        SoundManager.release()
    }
}


@Composable
fun SudokuApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("home") {
            HomeScreen(navController = navController, modifier = modifier)
        }

        composable("sudokuScreen/{mode}/{level}") { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode") ?: "free"
            val level = backStackEntry.arguments?.getString("level") ?: stringResource(R.string.difficulty_easy)

            when (mode) {
                "free" -> {
                    SudokuGameScreen(
                        navController = navController,
                        modifier = modifier,
                        level = level,
                        mode = mode
                    )
                }
                "challenge" -> {
                    SudokuChallengeGameScreen(
                        navController = navController,
                        modifier = modifier,
                        level = level,
                        mode = mode
                    )
                }
                else -> {
                    // Nếu có mode sai, mặc định về free
                    SudokuGameScreen(
                        navController = navController,
                        modifier = modifier,
                        level = level,
                        mode = "free"
                    )
                }
            }
        }
    }
}


@Composable
fun HomeScreen(modifier: Modifier, navController: NavController) {
    var showDifficultyDialog by remember { mutableStateOf(false) }
    var currentMode by remember { mutableStateOf("free") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xA403A9F4)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.Black
            ),
            modifier = Modifier.padding(bottom = 48.dp)
        )

        GameModeButton(
            icon = Icons.Default.PlayArrow,
            text = stringResource(R.string.casual),
            description = stringResource(R.string.game_mode_free_desc),
            onClick = {
                SoundManager.playClickSound()
                currentMode = "free"
                showDifficultyDialog = true
            },
            modifier = Modifier.padding(bottom = 24.dp)
        )

        GameModeButton(
            icon = Icons.Default.Timer,
            text = stringResource(R.string.challenge),
            description = stringResource(R.string.game_mode_challenge_desc),
            onClick = {
                SoundManager.playClickSound()
                currentMode = "challenge"
                showDifficultyDialog = true
            }
        )
    }

    AnimatedVisibility(
        visible = showDifficultyDialog,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(200))
    ) {
        DifficultyDialog(
            onDifficultySelected = { difficulty ->
                showDifficultyDialog = false
                navigateToGame(navController, currentMode, difficulty)
            },
            onDismiss = { showDifficultyDialog = false }
        )
    }
}

@Composable
fun GameModeButton(
    icon: ImageVector,
    text: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        shadowElevation = 4.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun DifficultyDialog(
    onDifficultySelected: (Difficulty) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .width(300.dp)
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.choose_difficulty),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.close))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Difficulty.values().forEach { difficulty ->
                    DifficultyItem(
                        difficulty = difficulty,
                        isSelected = false,
                        onClick = {
                            SoundManager.playClickSound()
                            onDifficultySelected(difficulty)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun DifficultyItem(
    difficulty: Difficulty,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) Color(0xFF4CAF50) else Color.LightGray
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = difficulty.icon,
                contentDescription = null,
                tint = difficulty.color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = difficulty.displayName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = difficulty.color
                    )
                )
                Text(
                    text = difficulty.description,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

fun navigateToGame(navController: NavController, mode: String, difficulty: Difficulty) {
    navController.navigate("sudokuScreen/$mode/${difficulty.displayName}")
}

enum class Difficulty(
    val displayName: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val emptyCells: Int
) {
    EASY(
        "Dễ",
        "Cho người mới bắt đầu",
        Icons.Default.SentimentVerySatisfied,
        Color(0xFF4CAF50),
        30
    ),
    MEDIUM(
        "Trung bình",
        "Thử thách vừa phải",
        Icons.Default.SentimentNeutral,
        Color(0xFF2196F3),
        45
    ),
    HARD(
        "Khó",
        "Dành cho cao thủ",
        Icons.Default.SentimentVeryDissatisfied,
        Color(0xFFF44336),
        60
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    SudokuMobileAppTheme {
        val navController = rememberNavController()
        HomeScreen(
            modifier = Modifier,
            navController = navController
        )
    }
}