package com.morningstar.pulsarpop.ui.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.morningstar.pulsarpop.ui.common.CosmicStarfield
import com.morningstar.pulsarpop.ui.theme.PulsarPopTheme
import com.morningstar.pulsarpop.ui.theme.getTileColors
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    state: GameUiState,
    onTileClick: (Int, Int) -> Unit,
    onRestart: () -> Unit,
    onNextLevel: () -> Unit,
    onBackPressed: () -> Unit,
    onShowExitDialog: () -> Unit = onBackPressed,
    onDismissExitDialog: () -> Unit = {},
    onConfirmExit: () -> Unit = {},
    onDismissShuffleBanner: () -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current
    val tileColors = getTileColors()

    // Ambient background pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "backgroundPulse")
    val ambientPulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ambientPulse"
    )

    val pulsarRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing)
        ),
        label = "pulsarRotation"
    )

    // Animated score change indicator
    var previousScore by remember { mutableIntStateOf(state.score) }
    var scoreAnimationTrigger by remember { mutableStateOf(false) }

    LaunchedEffect(state.score) {
        if (state.score > previousScore) {
            scoreAnimationTrigger = true
            previousScore = state.score
        }
    }

    val scoreScale by animateFloatAsState(
        targetValue = if (scoreAnimationTrigger) 1.25f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        finishedListener = { scoreAnimationTrigger = false },
        label = "scoreScale"
    )

    // Auto-dismiss shuffle banner after 3 seconds
    LaunchedEffect(state.showShuffleBanner) {
        if (state.showShuffleBanner) {
            delay(3000L)
            onDismissShuffleBanner()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surfaceContainer
                    )
                )
            )
    ) {
        // Cosmic Animated Starfield Background
        CosmicStarfield(pulseFactor = ambientPulse)

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Rounded.Star,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .rotate(pulsarRotation)
                                    )
                                }
                            }
                            Text(
                                text = "LEVEL ${state.level}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onShowExitDialog) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onRestart) {
                            Icon(
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = "Restart",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Modern Glassmorphism 3D HUD Dashboard
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.9f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)
                            )
                        )
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Score & High Score
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "SCORE",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (state.comboCount > 1) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.tertiaryContainer
                                    ) {
                                        Text(
                                            text = "${state.comboCount}x COMBO",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${state.score}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.scale(scoreScale)
                                )
                                state.floatingScoreText?.let { popText ->
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.primary,
                                        shadowElevation = 4.dp
                                    ) {
                                        Text(
                                            text = popText,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Star,
                                    contentDescription = "High Score",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "BEST: ${state.highScore}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Progress Metric
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "CLEAR TARGET",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${state.clearedCount} / ${state.targetCleared}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            // Progress bar indicator
                            LinearProgressIndicator(
                                progress = { (state.clearedCount.toFloat() / state.targetCleared.toFloat()).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .width(110.dp)
                                    .height(8.dp)
                                    .clip(CircleShape),
                                color = MaterialTheme.colorScheme.secondary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }

                // Shuffle Notification Banner
                AnimatedVisibility(
                    visible = state.showShuffleBanner,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        tonalElevation = 6.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clickable { onDismissShuffleBanner() }
                    ) {
                        Text(
                            text = "🌌 WARP SHUFFLE! No moves remained. Grid re-energized!",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 3D Glassmorphism Board Frame
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .fillMaxWidth()
                        .shadow(16.dp, RoundedCornerShape(24.dp), ambientColor = MaterialTheme.colorScheme.primary)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.95f),
                                    MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.95f)
                                )
                            )
                        )
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.6f)
                                )
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.grid.isNotEmpty()) {
                        val rows = state.grid.size
                        val cols = state.grid[0].size

                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            for (r in 0 until rows) {
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    for (c in 0 until cols) {
                                        val tile = state.grid[r][c]
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxHeight()
                                        ) {
                                            if (tile != null) {
                                                Glossy3DTile(
                                                    color = tileColors[tile.colorIndex % tileColors.size],
                                                    onClick = {
                                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                        onTileClick(r, c)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Footer Guidance
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Text(
                        text = "⚡ Tap 2+ connected tiles to pop!",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                    )
                }
            }
        }

        // Level Complete Celebration Modal
        if (state.isLevelComplete) {
            LevelCompleteModal(
                level = state.level,
                score = state.score,
                onNextLevel = onNextLevel
            )
        }

        // Exit Confirmation Dialog
        if (state.showExitDialog) {
            AlertDialog(
                onDismissRequest = onDismissExitDialog,
                confirmButton = {
                    Button(
                        onClick = onConfirmExit,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("QUIT GAME")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = onDismissExitDialog) {
                        Text("RESUME")
                    }
                },
                title = {
                    Text(
                        text = "ABANDON MISSION?",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black
                    )
                },
                text = {
                    Text(
                        text = "Are you sure you want to exit to the main quadrant? Active level progress will be lost.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                shape = RoundedCornerShape(24.dp)
            )
        }
    }
}

@Composable
fun Glossy3DTile(
    color: Color,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessHigh),
        label = "tileScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .scale(scale)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(14.dp),
                clip = false,
                ambientColor = color,
                spotColor = color
            )
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        color.copy(alpha = 0.95f),
                        color,
                        Color(
                            red = (color.red * 0.7f).coerceIn(0f, 1f),
                            green = (color.green * 0.7f).coerceIn(0f, 1f),
                            blue = (color.blue * 0.7f).coerceIn(0f, 1f),
                            alpha = color.alpha
                        )
                    ),
                    center = Offset(80f, 80f),
                    radius = 300f
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.65f),
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.35f)
                    )
                ),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    isPressed = true
                    onClick()
                }
            ),
        contentAlignment = Alignment.TopStart
    ) {
        // Top-left glossy reflection lens cap
        Box(
            modifier = Modifier
                .padding(top = 3.dp, start = 4.dp)
                .fillMaxWidth(0.55f)
                .height(10.dp)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.65f),
                            Color.White.copy(alpha = 0.05f)
                        )
                    )
                )
        )
    }
}

@Composable
fun LevelCompleteModal(
    level: Int,
    score: Int,
    onNextLevel: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "starSpin")
    val starRotate by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing)),
        label = "starRotate"
    )

    AlertDialog(
        onDismissRequest = {},
        confirmButton = {
            Button(
                onClick = onNextLevel,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "NEXT LEVEL 🚀",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Rounded.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier
                        .size(56.dp)
                        .rotate(starRotate)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "LEVEL $level COMPLETE!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Stellar work! You crushed the energy grid.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "TOTAL SCORE",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$score",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        },
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Preview(showBackground = true)
@Composable
fun GameScreen3DPreview() {
    PulsarPopTheme {
        GameScreen(
            state = GameUiState(
                grid = listOf(
                    listOf(Tile(1, 0, 0, 0), Tile(2, 1, 0, 1), Tile(3, 2, 0, 2)),
                    listOf(Tile(4, 1, 1, 0), Tile(5, 0, 1, 1), Tile(6, 2, 1, 2)),
                    listOf(Tile(7, 2, 2, 0), Tile(8, 0, 2, 1), Tile(9, 1, 2, 2))
                ),
                rows = 3,
                cols = 3,
                score = 3450,
                highScore = 5200,
                level = 3,
                clearedCount = 18,
                targetCleared = 25,
                comboCount = 2,
                floatingScoreText = "+360"
            ),
            onTileClick = { _, _ -> },
            onRestart = {},
            onNextLevel = {},
            onBackPressed = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenLevelCompletePreview() {
    PulsarPopTheme {
        GameScreen(
            state = GameUiState(
                grid = emptyList(),
                score = 8500,
                level = 4,
                isLevelComplete = true
            ),
            onTileClick = { _, _ -> },
            onRestart = {},
            onNextLevel = {},
            onBackPressed = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenExitDialogPreview() {
    PulsarPopTheme {
        GameScreen(
            state = GameUiState(
                grid = listOf(
                    listOf(Tile(1, 0, 0, 0), Tile(2, 1, 0, 1)),
                    listOf(Tile(3, 1, 1, 0), Tile(4, 0, 1, 1))
                ),
                showExitDialog = true
            ),
            onTileClick = { _, _ -> },
            onRestart = {},
            onNextLevel = {},
            onBackPressed = {}
        )
    }
}
