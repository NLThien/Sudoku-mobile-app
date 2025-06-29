@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.sudokumobileapp.ui.screens.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.sudokumobileapp.data.repository.ThemePreferences
import com.example.sudokumobileapp.ui.screen.formatTime
import kotlinx.coroutines.launch
import androidx.compose.runtime.LaunchedEffect
import androidx. compose. runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.example.sudokumobileapp.ui.theme.SudokuMobileAppTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.rememberCoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope() // Thêm scope
    var isDarkTheme by rememberSaveable { mutableStateOf(false) }

    // Tải theme từ DataStore
    LaunchedEffect(Unit) {
        isDarkTheme = ThemePreferences.loadTheme(context)
    }

    SudokuMobileAppTheme(darkTheme = isDarkTheme) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Cài Đặt") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                // Section chọn theme
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Chế độ tối",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { newValue ->
                            isDarkTheme = newValue
                            scope.launch {
                                ThemePreferences.saveTheme(context, newValue)
                            }
                        }
                    )
                }
            }
        }
    }
}