package com.morningstar.pulsarpop.data.room

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepository @Inject constructor(
    private val gameDao: GameDao
) {
    val gameStatsFlow: Flow<GameEntity> = gameDao.getGameStats()
        .map { it ?: GameEntity() }

    val highScoreFlow: Flow<Int> = gameStatsFlow.map { it.highScore }
    val currentLevelFlow: Flow<Int> = gameStatsFlow.map { it.currentLevel }

    suspend fun updateHighScoreAndLevel(score: Int, level: Int) {
        val current = gameDao.getGameStatsDirect()
        if (current == null) {
            val newStats = GameEntity(
                id = 1,
                highScore = score,
                currentLevel = level,
                maxLevelReached = level
            )
            gameDao.upsertGameStats(newStats)
        } else {
            val updatedHighScore = maxOf(current.highScore, score)
            val updatedMaxLevel = maxOf(current.maxLevelReached, level)
            gameDao.upsertGameStats(
                current.copy(
                    highScore = updatedHighScore,
                    currentLevel = level,
                    maxLevelReached = updatedMaxLevel
                )
            )
        }
    }

    suspend fun saveHighScore(score: Int) {
        val current = gameDao.getGameStatsDirect() ?: GameEntity()
        if (score > current.highScore) {
            gameDao.upsertGameStats(current.copy(highScore = score))
        }
    }

    suspend fun saveCurrentLevel(level: Int) {
        val current = gameDao.getGameStatsDirect() ?: GameEntity()
        val maxLevel = maxOf(current.maxLevelReached, level)
        gameDao.upsertGameStats(current.copy(currentLevel = level, maxLevelReached = maxLevel))
    }

    suspend fun recordTilePops(poppedCount: Int) {
        val current = gameDao.getGameStatsDirect() ?: GameEntity()
        gameDao.upsertGameStats(current.copy(totalTilesPopped = current.totalTilesPopped + poppedCount))
    }
}
