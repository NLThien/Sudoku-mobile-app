package com.example.sudokumobileapp.ui.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.rounded.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.sudokumobileapp.R


@Composable
fun NotificationExit(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val translateY by animateFloatAsState(
        targetValue = if (animationPlayed) 0f else 100f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f),
        label = "dialog_animation"
    )

    LaunchedEffect(Unit) { animationPlayed = true }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer {
                    translationY = translateY
                    alpha = translateY / 100f + 0.3f
                }
                .shadow(24.dp, RoundedCornerShape(20.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF9C27B0), Color(0xFF6A1B9A))
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier.width(280.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = null,
                    tint = Color(0xFFFFEB3B),
                    modifier = Modifier.size(64.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.oh_no),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.cancel_not_save),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White.copy(alpha = 0.9f)
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.cancel),
                            color = Color.White
                        )
                    }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF5252),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(stringResource(R.string.exit_game))
                    }
                }
            }
        }
    }
}


@Composable
fun PauseMenu(
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(300),
        label = "fade_animation"
    )
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 500f),
        label = "scale_animation"
    )

    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f * alpha))
            .clickable(enabled = visible) { onResume() },
        contentAlignment = Alignment.Center
    ) {
        Dialog(onDismissRequest = onResume) {
            Surface(
                modifier = modifier
                    .width(300.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    },
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.pause),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    PauseMenuButton(
                        text = stringResource(id = R.string.resume),
                        icon = Icons.Default.PlayArrow,
                        onClick = onResume,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    PauseMenuButton(
                        text = stringResource(id = R.string.restart),
                        icon = Icons.Default.Refresh,
                        onClick = onRestart,
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = MaterialTheme.colorScheme.secondaryContainer
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    PauseMenuButton(
                        text = stringResource(id = R.string.exit_game),
                        icon = Icons.AutoMirrored.Filled.ExitToApp,
                        onClick = onExit,
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = MaterialTheme.colorScheme.errorContainer
                    )
                }
            }
        }
    }
}


@Composable
fun PauseMenuButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(100),
        label = "button_scale"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .height(54.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale },
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        shape = RoundedCornerShape(12.dp),
        interactionSource = remember { MutableInteractionSource() }.also { interactionSource ->
            LaunchedEffect(interactionSource) {
                interactionSource.interactions.collect {
                    when (it) {
                        is PressInteraction.Press -> isPressed = true
                        is PressInteraction.Release, is PressInteraction.Cancel -> isPressed = false
                    }
                }
            }
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium)
            )
        }
    }
}


@Composable
fun SudokuWinDialog(
    time: Long,
    onRestart: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    var showDialog by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (showDialog) 1f else 0.9f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "dialog_scale"
    )

    LaunchedEffect(Unit) { showDialog = true }

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Surface(
            modifier = modifier
                .graphicsLayer { scaleX = scale; scaleY = scale }
                .shadow(16.dp, shape),
            shape = shape,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Rounded.Stars,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.game_completed),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${stringResource(R.string.time_up)} ${formatTime(time)}",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onExit,
                        modifier = Modifier.weight(1f),
                        shape = shape
                    ) {
                        Text(stringResource(id = R.string.exit))
                    }

                    Button(
                        onClick = onRestart,
                        modifier = Modifier.weight(1f),
                        shape = shape
                    ) {
                        Text(stringResource(id = R.string.restart))
                    }
                }
            }
        }
    }
}
