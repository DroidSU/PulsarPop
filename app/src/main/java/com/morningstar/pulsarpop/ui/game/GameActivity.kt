package com.morningstar.pulsarpop.ui.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.morningstar.pulsarpop.ui.theme.PulsarPopTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PulsarPopTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val state by viewModel.uiState.collectAsState()

                    GameScreen(
                        state = state,
                        onTileClick = { r, c -> viewModel.onTileClicked(r, c) },
                        onRestart = { viewModel.startNewGame() },
                        onNextLevel = { viewModel.advanceLevel() },
                        onBackPressed = { viewModel.showExitConfirmationDialog(true) },
                        onShowExitDialog = { viewModel.showExitConfirmationDialog(true) },
                        onDismissExitDialog = { viewModel.showExitConfirmationDialog(false) },
                        onConfirmExit = { finish() },
                        onDismissShuffleBanner = { viewModel.dismissShuffleBanner() }
                    )
                }
            }
        }
    }
}
