package com.example.sudokumobileapp.ui.screens.game.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import android.R.attr.fontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import com.example.sudokumobileapp.data.local.entity.GameRecord
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale
import kotlinx.coroutines.launch
import com.example.sudokumobileapp.ui.screen.formatTime
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    onBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    TopAppBar(
        title = {
            Text(
                text = "Thành Tích",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "Back")
            }
        },
        actions = {
            // Nút xóa lịch sử
            IconButton(
                onClick = {
                    scope.launch {
                        // Xóa tất cả các bản ghi từ cơ sở dữ liệu
                    }
                }
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Xóa lịch sử"
                )
            }
        }
    )
}
// không có cơ hội dùng
@Composable
fun GameRecordItem(record: GameRecord) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Chế độ: ${getModeName(record.mode)}",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Độ khó: ${record.difficulty}",
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Thời gian: ${formatTime(record.completionTime)}")
                Text("Lỗi: ${record.errorCount}")
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text("Gợi ý đã dùng: ${record.hintCount}")

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                "Hoàn thành: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(record.timestamp))}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

private fun getModeName(mode: String): String = when(mode) {
    "free" -> "Tự do"
    "challenge" -> "Thử thách"
    else -> mode
}