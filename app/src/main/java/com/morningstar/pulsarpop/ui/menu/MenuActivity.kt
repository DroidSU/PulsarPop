package com.morningstar.pulsarpop.ui.menu

import android.content.Intent
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
import com.morningstar.pulsarpop.ui.auth.AuthActivity
import com.morningstar.pulsarpop.ui.game.GameActivity
import com.morningstar.pulsarpop.ui.theme.PulsarPopTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuActivity : ComponentActivity() {

    private val viewModel: MenuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PulsarPopTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val highScore by viewModel.highScore.collectAsState()

                    MenuScreen(
                        highScore = highScore,
                        onStartGame = {
                            val intent = Intent(this, GameActivity::class.java)
                            startActivity(intent)
                        },
                        onOpenAuth = {
                            val intent = Intent(this, AuthActivity::class.java)
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}
