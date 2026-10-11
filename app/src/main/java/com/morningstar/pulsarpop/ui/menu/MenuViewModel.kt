package com.morningstar.pulsarpop.ui.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.morningstar.pulsarpop.data.PreferencesRepository
import com.morningstar.pulsarpop.data.room.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    gameRepository: GameRepository,
    preferencesRepository: PreferencesRepository
) : ViewModel() {

    val highScore: StateFlow<Int> = gameRepository.highScoreFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val currentLevel: StateFlow<Int> = gameRepository.currentLevelFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 1
        )

    val hapticsEnabled: StateFlow<Boolean> = preferencesRepository.hapticsEnabledFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    val soundEnabled: StateFlow<Boolean> = preferencesRepository.soundEnabledFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )
}
