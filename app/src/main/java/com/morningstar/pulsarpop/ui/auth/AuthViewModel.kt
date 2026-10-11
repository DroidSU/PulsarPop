package com.morningstar.pulsarpop.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.morningstar.pulsarpop.data.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val phoneNumber: String = "",
    val otpCode: String = "",
    val isOtpSent: Boolean = false,
    val isLoading: Boolean = false,
    val statusMessage: String? = null,
    val isAuthenticated: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onPhoneNumberChanged(number: String) {
        _uiState.update { it.copy(phoneNumber = number, statusMessage = null) }
    }

    fun onOtpCodeChanged(code: String) {
        _uiState.update { it.copy(otpCode = code, statusMessage = null) }
    }

    fun requestOtp() {
        if (_uiState.value.phoneNumber.length < 10) {
            _uiState.update { it.copy(statusMessage = "Please enter a valid phone number") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, statusMessage = null) }
            delay(1200) // Placeholder simulated network call
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isOtpSent = true,
                    statusMessage = "OTP sent to ${it.phoneNumber}"
                )
            }
        }
    }

    fun verifyOtp() {
        if (_uiState.value.otpCode.length < 4) {
            _uiState.update { it.copy(statusMessage = "Please enter 4-digit OTP code") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, statusMessage = null) }
            delay(1200) // Placeholder verification delay
            preferencesRepository.setLoggedIn(true)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isAuthenticated = true,
                    statusMessage = "Success! Verified via Phone."
                )
            }
        }
    }

    fun loginWithGoogle() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, statusMessage = null) }
            delay(1500) // Placeholder Google One Tap auth flow delay
            preferencesRepository.setLoggedIn(true)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isAuthenticated = true,
                    statusMessage = "Quick Login with Google successful!"
                )
            }
        }
    }

    fun proxyLogin() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = false, isAuthenticated = true, statusMessage = "Proxy Login Successful!")
            }
        }
    }
}
