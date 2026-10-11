package com.morningstar.pulsarpop.ui.game

data class Tile(
    val id: Long,
    val colorIndex: Int,
    val row: Int,
    val col: Int
)

data class GameUiState(
    val grid: List<List<Tile?>> = emptyList(),
    val rows: Int = 6,
    val cols: Int = 6,
    val score: Int = 0,
    val level: Int = 1,
    val targetScore: Int = 1000,
    val colorCount: Int = 3,
    val isGameOver: Boolean = false,
    val isLevelComplete: Boolean = false,
    val clearedCount: Int = 0,
    val targetCleared: Int = 25
)
