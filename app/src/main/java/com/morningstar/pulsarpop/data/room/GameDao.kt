package com.morningstar.pulsarpop.data.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM game_stats WHERE id = 1 LIMIT 1")
    fun getGameStats(): Flow<GameEntity?>

    @Query("SELECT * FROM game_stats WHERE id = 1 LIMIT 1")
    suspend fun getGameStatsDirect(): GameEntity?

    @Upsert
    suspend fun upsertGameStats(gameEntity: GameEntity): Long

    @Query("UPDATE game_stats SET highScore = MAX(highScore, :score), currentLevel = :level, maxLevelReached = MAX(maxLevelReached, :level) WHERE id = 1")
    suspend fun updateScoreAndLevel(score: Int, level: Int): Int
}
