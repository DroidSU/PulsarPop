package com.morningstar.pulsarpop.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "pulsar_pop_preferences")

@Singleton
class PreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val HIGH_SCORE = intPreferencesKey("high_score")
        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    }

    val highScoreFlow: Flow<Int> = context.dataStore.data
        .map { preferences -> preferences[PreferencesKeys.HIGH_SCORE] ?: 0 }

    val hapticsEnabledFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[PreferencesKeys.HAPTICS_ENABLED] ?: true }

    val soundEnabledFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[PreferencesKeys.SOUND_ENABLED] ?: true }

    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[PreferencesKeys.IS_LOGGED_IN] ?: false }

    suspend fun saveHighScore(score: Int) {
        context.dataStore.edit { preferences ->
            val currentHigh = preferences[PreferencesKeys.HIGH_SCORE] ?: 0
            if (score > currentHigh) {
                preferences[PreferencesKeys.HIGH_SCORE] = score
            }
        }
    }

    suspend fun setLoggedIn(isLoggedIn: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] = isLoggedIn
        }
    }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAPTICS_ENABLED] = enabled
        }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SOUND_ENABLED] = enabled
        }
    }
}
