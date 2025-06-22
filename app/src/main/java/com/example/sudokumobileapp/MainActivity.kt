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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SentimentNeutral
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sudokumobileapp.ui.screen.SudokuGameScreen
import com.example.sudokumobileapp.ui.theme.SudokuMobileAppTheme
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SudokuMobileAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Gọi SudokuApp và truyền Modifier với padding
                    SudokuApp(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun SudokuApp(modifier: Modifier = Modifier) {
   val navController = rememberNavController()

    NavHost (navController= navController, startDestination = "home",modifier= modifier){
        composable("home") {
            HomeScreen(navController=navController, modifier = modifier)
        }
        composable("sudokuScreen/{level}") {backStackEntry ->
            SudokuGameScreen(level = backStackEntry.arguments?.getString("level") ?: "Dễ", navController = navController,modifier= modifier)
        }
    }

}


@Composable
fun HomeScreen(modifier: Modifier,navController: NavController){
    var selectedDifficulty by remember { mutableStateOf<Difficulty?>(null) }
    var showCustomDialog by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                showCustomDialog = true
            },
            modifier = Modifier.padding(bottom = 16.dp)
            ) {
            CustomButton(text = "Tự do")
        }
        Button(
            onClick = {},
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            CustomButton(text = "Thử thách")
        }
        AnimatedVisibility(
            visible = showCustomDialog,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(200))
        ) {
            DifficultyDialog(
                currentDifficulty = selectedDifficulty,
                onDifficultySelected = {
                    selectedDifficulty = it
                    showCustomDialog = false
                },
                onDismiss = { showCustomDialog = false },
                navController
            )
        }
    }
}
@Composable
fun CustomButton(text : String){
    Row(
        modifier = Modifier
            .fillMaxWidth(0.4f),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text= text,
            fontWeight = FontWeight(600),
            fontSize = 24.sp
        )
    }
    
}


@Composable
fun DifficultyDialog(
    currentDifficulty: Difficulty?,
    onDifficultySelected: (Difficulty) -> Unit,
    onDismiss: () -> Unit,
    navController: NavController
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
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CHỌN ĐỘ KHÓ",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Đóng")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Difficulty Options
                Difficulty.values().forEach { difficulty ->
                    DifficultyItem(
                        difficulty = difficulty,
                        isSelected = currentDifficulty == difficulty,
                        onClick = {
                            onDifficultySelected(difficulty)
                            navController.navigate("sudokuScreen/${difficulty.displayName}")
                        },


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
            if (isSelected) {
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Đã chọn",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
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
        Color(0xFF4CAF50), // Green
        30
    ),
    MEDIUM(
        "Trung bình",
        "Thử thách vừa phải",
        Icons.Default.SentimentNeutral,
        Color(0xFF2196F3), // Blue
        45
    ),
    HARD(
        "Khó",
        "Dành cho cao thủ",
        Icons.Default.SentimentVeryDissatisfied,
        Color(0xFFF44336), // Red
        60
    )
}