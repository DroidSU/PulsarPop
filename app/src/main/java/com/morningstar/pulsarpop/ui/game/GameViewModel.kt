package com.morningstar.pulsarpop.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.morningstar.pulsarpop.data.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class GameViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var nextTileId = 1L

    init {
        startNewGame()
    }

    fun startNewGame() {
        val rows = 6
        val cols = 6
        val colorCount = 3
        val initialGrid = generateGrid(rows, cols, colorCount)
        _uiState.value = GameUiState(
            grid = initialGrid,
            rows = rows,
            cols = cols,
            score = 0,
            level = 1,
            targetScore = 800,
            colorCount = colorCount,
            isGameOver = false,
            isLevelComplete = false,
            clearedCount = 0,
            targetCleared = 20
        )
    }

    fun onTileClicked(row: Int, col: Int) {
        val currentState = _uiState.value
        if (currentState.isGameOver || currentState.isLevelComplete) return

        val grid = currentState.grid
        if (row !in grid.indices || col !in grid[row].indices) return
        val clickedTile = grid[row][col] ?: return

        val cluster = findConnectedCluster(grid, row, col, clickedTile.colorIndex)
        if (cluster.size < 2) return

        viewModelScope.launch {
            var newGrid = removeCluster(grid, cluster)
            newGrid = applyGravityAndRefill(newGrid, currentState.colorCount)

            val pointsEarned = cluster.size * cluster.size * 15
            val newScore = currentState.score + pointsEarned
            val newClearedCount = currentState.clearedCount + cluster.size

            val levelComplete = newClearedCount >= currentState.targetCleared

            _uiState.update {
                it.copy(
                    grid = newGrid,
                    score = newScore,
                    clearedCount = newClearedCount,
                    isLevelComplete = levelComplete
                )
            }

            preferencesRepository.saveHighScore(newScore)

            if (!levelComplete && isBoardStuck(newGrid, currentState.colorCount)) {
                val shuffledGrid = generateGrid(currentState.rows, currentState.cols, currentState.colorCount)
                _uiState.update { it.copy(grid = shuffledGrid) }
            }
        }
    }

    fun advanceLevel() {
        val current = _uiState.value
        val nextLevel = current.level + 1
        val rows = minOf(8, 6 + (nextLevel / 2))
        val cols = minOf(8, 6 + (nextLevel / 2))
        val colorCount = minOf(5, 3 + (nextLevel / 3))
        val targetCleared = 20 + (nextLevel * 5)
        val targetScore = current.targetScore + (nextLevel * 500)
        val newGrid = generateGrid(rows, cols, colorCount)

        _uiState.value = current.copy(
            grid = newGrid,
            rows = rows,
            cols = cols,
            level = nextLevel,
            targetScore = targetScore,
            colorCount = colorCount,
            isLevelComplete = false,
            clearedCount = 0,
            targetCleared = targetCleared
        )
    }

    private fun generateGrid(rows: Int, cols: Int, colorCount: Int): List<List<Tile?>> {
        val mutableGrid = mutableListOf<MutableList<Tile?>>()
        for (r in 0 until rows) {
            val rowList = mutableListOf<Tile?>()
            for (c in 0 until cols) {
                rowList.add(
                    Tile(
                        id = nextTileId++,
                        colorIndex = Random.nextInt(colorCount),
                        row = r,
                        col = c
                    )
                )
            }
            mutableGrid.add(rowList)
        }
        return mutableGrid
    }

    private fun findConnectedCluster(
        grid: List<List<Tile?>>,
        startRow: Int,
        startCol: Int,
        colorIndex: Int
    ): List<Tile> {
        val visited = Array(grid.size) { BooleanArray(grid[0].size) }
        val cluster = mutableListOf<Tile>()
        val queue = ArrayDeque<Pair<Int, Int>>()

        queue.add(Pair(startRow, startCol))
        visited[startRow][startCol] = true

        val dr = arrayOf(-1, 1, 0, 0)
        val dc = arrayOf(0, 0, -1, 1)

        while (queue.isNotEmpty()) {
            val (r, c) = queue.removeFirst()
            val tile = grid[r][c] ?: continue
            if (tile.colorIndex == colorIndex) {
                cluster.add(tile)
                for (i in 0..3) {
                    val nr = r + dr[i]
                    val nc = c + dc[i]
                    if (nr in grid.indices && nc in grid[0].indices && !visited[nr][nc]) {
                        visited[nr][nc] = true
                        val neighbor = grid[nr][nc]
                        if (neighbor != null && neighbor.colorIndex == colorIndex) {
                            queue.add(Pair(nr, nc))
                        }
                    }
                }
            }
        }
        return cluster
    }

    private fun removeCluster(grid: List<List<Tile?>>, cluster: List<Tile>): List<List<Tile?>> {
        val newGrid = grid.map { row -> row.map { it?.copy() }.toMutableList() }
        val clusterCoords = cluster.map { Pair(it.row, it.col) }.toSet()

        for ((r, c) in clusterCoords) {
            newGrid[r][c] = null
        }
        return newGrid
    }

    private fun applyGravityAndRefill(grid: List<List<Tile?>>, colorCount: Int): List<List<Tile?>> {
        val rows = grid.size
        val cols = grid[0].size
        val newGrid = Array(rows) { arrayOfNulls<Tile>(cols) }

        for (c in 0 until cols) {
            val columnTiles = mutableListOf<Tile>()
            for (r in 0 until rows) {
                grid[r][c]?.let { columnTiles.add(it) }
            }

            var targetRow = rows - 1
            for (i in columnTiles.size - 1 downTo 0) {
                val tile = columnTiles[i]
                newGrid[targetRow][c] = tile.copy(row = targetRow, col = c)
                targetRow--
            }
            while (targetRow >= 0) {
                newGrid[targetRow][c] = Tile(
                    id = nextTileId++,
                    colorIndex = Random.nextInt(colorCount),
                    row = targetRow,
                    col = c
                )
                targetRow--
            }
        }

        return newGrid.map { it.toList() }
    }

    private fun isBoardStuck(grid: List<List<Tile?>>, colorCount: Int): Boolean {
        val rows = grid.size
        val cols = grid[0].size
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val tile = grid[r][c] ?: continue
                if (c + 1 < cols && grid[r][c + 1]?.colorIndex == tile.colorIndex) return false
                if (r + 1 < rows && grid[r + 1][c]?.colorIndex == tile.colorIndex) return false
            }
        }
        return true
    }
}
