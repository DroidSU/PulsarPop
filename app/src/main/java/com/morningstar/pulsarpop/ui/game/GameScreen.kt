package com.morningstar.pulsarpop.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.morningstar.pulsarpop.ui.theme.PulsarPopTheme

val TileColors = listOf(
    Color(0xFFFF5252), // Vibrant Red / Magenta
    Color(0xFF448AFF), // Electric Blue
    Color(0xFF69F0AE), // Stellar Green
    Color(0xFFFFD740), // Solar Gold
    Color(0xFFE040FB)  // Cosmic Purple
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    state: GameUiState,
    onTileClick: (Int, Int) -> Unit,
    onRestart: () -> Unit,
    onNextLevel: () -> Unit,
    onBackPressed: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Level ${state.level}") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onRestart) {
                        Icon(Icons.Default.Refresh, contentDescription = "Restart")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "SCORE", style = MaterialTheme.typography.labelMedium)
                        Text(
                            text = "${state.score}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "PROGRESS", style = MaterialTheme.typography.labelMedium)
                        Text(
                            text = "${state.clearedCount} / ${state.targetCleared}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (state.grid.isNotEmpty()) {
                    val rows = state.grid.size
                    val cols = state.grid[0].size

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        for (r in 0 until rows) {
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                for (c in 0 until cols) {
                                    val tile = state.grid[r][c]
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                if (tile != null) {
                                                    TileColors[tile.colorIndex % TileColors.size]
                                                } else {
                                                    Color.Transparent
                                                }
                                            )
                                            .clickable(enabled = tile != null) {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                onTileClick(r, c)
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (tile != null) {
                                            Box(
                                                modifier = Modifier
                                                    .size(12.dp)
                                                    .clip(CircleShape)
                                                    .background(Color.White.copy(alpha = 0.4f))
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

            Text(
                text = "Tap 2+ connected matching tiles to pop!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
    }

    if (state.isLevelComplete) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Level ${state.level} Complete!") },
            text = { Text("Fantastic job! You reached the target cleared tiles. Ready for the next stellar challenge?") },
            confirmButton = {
                Button(onClick = onNextLevel) {
                    Text("NEXT LEVEL")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
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
                score = 1250,
                level = 2,
                clearedCount = 15,
                targetCleared = 25
            ),
            onTileClick = { _, _ -> },
            onRestart = {},
            onNextLevel = {},
            onBackPressed = {}
        )
    }
}
