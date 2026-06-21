package dev.sheershbhatnagar.ai_assistant.features.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.sheershbhatnagar.ai_assistant.core.network.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionManager = SessionManager(application)
    private val networkClient = NetworkClient(sessionManager)
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    private val _hostAddress = MutableStateFlow("http://10.0.2.2:8080")
    val hostAddress: StateFlow<String> = _hostAddress.asStateFlow()
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()
    private val _otp = MutableStateFlow("")
    val otp: StateFlow<String> = _otp.asStateFlow()

    init {
        viewModelScope.launch {
            sessionManager.hostAddress.collect { savedHost ->
                if (!savedHost.isNullOrBlank()) {
                    _hostAddress.value = savedHost
                }
            }
        }
    }

    fun updateHostAddress(host: String) {
        _hostAddress.value = host
    }

    fun updateEmail(email: String) {
        _email.value = email
    }

    fun updateOtp(otp: String) {
        _otp.value = otp
    }

    fun sendOtp() {
        if (_email.value.isBlank() || _hostAddress.value.isBlank()) {
            _authState.value = AuthState.Error("Email and Host Address cannot be empty")
            return
        }
        
        _authState.value = AuthState.Loading
        
        viewModelScope.launch {
            sessionManager.saveHostAddress(_hostAddress.value)
            
            val result: Result<Map<String, String>> = networkClient.post(
                "api/v1/auth/send-otp",
                SendOtpRequest(_email.value)
            )
            
            result.fold(
                onSuccess = {
                    _authState.value = AuthState.OtpSent
                },
                onFailure = { error ->
                    _authState.value = AuthState.Error(error.message ?: "Failed to send OTP")
                }
            )
        }
    }

    fun verifyOtp(onSuccess: () -> Unit) {
        if (_otp.value.isBlank()) {
            _authState.value = AuthState.Error("OTP cannot be empty")
            return
        }
        
        _authState.value = AuthState.Loading
        
        viewModelScope.launch {
            
            val result: Result<AuthResponse> = networkClient.post(
                "api/v1/auth/verify-otp",
                VerifyOtpRequest(_email.value, _otp.value)
            )
            
            result.fold(
                onSuccess = { authResponse ->
                    sessionManager.saveSession(
                        host = _hostAddress.value,
                        token = authResponse.token,
                        userIdStr = "",
                        email = _email.value,
                        firstName = "User"
                    )
                    
                    val profileResult: Result<UserProfileResponse> = networkClient.get("api/v1/users/profile")
                    
                    profileResult.fold(
                        onSuccess = { profile ->
                            sessionManager.saveSession(
                                host = _hostAddress.value,
                                token = authResponse.token,
                                userIdStr = profile.id,
                                email = profile.email,
                                firstName = profile.firstName
                            )
                            _authState.value = AuthState.Success(authResponse.token)
                            onSuccess()
                        },
                        onFailure = { error ->
                            sessionManager.saveSession(
                                host = _hostAddress.value,
                                token = authResponse.token,
                                userIdStr = "00000000-0000-0000-0000-000000000000",
                                email = _email.value,
                                firstName = "User"
                            )
                            _authState.value = AuthState.Success(authResponse.token)
                            onSuccess()
                        }
                    )
                },
                onFailure = { error ->
                    _authState.value = AuthState.Error(error.message ?: "Invalid OTP")
                }
            )
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
        _otp.value = ""
    }
}

sealed interface AuthState {
    object Idle : AuthState
    object Loading : AuthState
    object OtpSent : AuthState
    data class Success(val token: String) : AuthState
    data class Error(val message: String) : AuthState
}
