package com.morningstar.pulsarpop.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.morningstar.pulsarpop.ui.game.GameActivity
import com.morningstar.pulsarpop.ui.theme.PulsarPopTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {

    private val viewModel: AuthViewModel by viewModels()

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

                    LaunchedEffect(state.isAuthenticated) {
                        if (state.isAuthenticated) {
                            startActivity(Intent(this@AuthActivity, GameActivity::class.java))
                            finish()
                        }
                    }

                    AuthScreen(
                        state = state,
                        onPhoneNumberChanged = { viewModel.onPhoneNumberChanged(it) },
                        onOtpCodeChanged = { viewModel.onOtpCodeChanged(it) },
                        onRequestOtp = { viewModel.requestOtp() },
                        onVerifyOtp = { viewModel.verifyOtp() },
                        onGoogleLogin = {
//                            viewModel.loginWithGoogle()
                            viewModel.proxyLogin()
                        },
                        onBackPressed = { finish() }
                    )
                }
            }
        }
    }
}
