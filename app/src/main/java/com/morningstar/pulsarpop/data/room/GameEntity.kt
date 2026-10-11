package com.morningstar.pulsarpop.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_stats")
data class GameEntity(
    @PrimaryKey
    val id: Int = 1,
    val highScore: Int = 0,
    val currentLevel: Int = 1,
    val maxLevelReached: Int = 1,
    val totalTilesPopped: Int = 0,
    val gamesPlayed: Int = 0
)
