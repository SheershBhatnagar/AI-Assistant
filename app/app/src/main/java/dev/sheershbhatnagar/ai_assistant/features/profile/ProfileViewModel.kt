package dev.sheershbhatnagar.ai_assistant.features.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.sheershbhatnagar.ai_assistant.core.network.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(application)
    private val networkClient = NetworkClient(sessionManager)

    private val _userEmail = MutableStateFlow("")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userLastName = MutableStateFlow("")
    val userLastName: StateFlow<String> = _userLastName.asStateFlow()

    private val _models = MutableStateFlow<List<AiModelResponse>>(emptyList())
    val models: StateFlow<List<AiModelResponse>> = _models.asStateFlow()

    private val _defaultModelId = MutableStateFlow<String?>(null)
    val defaultModelId: StateFlow<String?> = _defaultModelId.asStateFlow()

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    val isDarkTheme = MutableStateFlow(true)

    init {
        viewModelScope.launch {
            sessionManager.userEmail.collect { email -> _userEmail.value = email ?: "" }
        }
        viewModelScope.launch {
            sessionManager.userFirstName.collect { name -> _userName.value = name ?: "" }
        }
        viewModelScope.launch {
            sessionManager.userLastName.collect { lastName -> _userLastName.value = lastName ?: "" }
        }
        viewModelScope.launch {
            sessionManager.isDarkTheme.collect { isDark -> isDarkTheme.value = isDark }
        }
        viewModelScope.launch {
            sessionManager.defaultModelId.collect { id -> _defaultModelId.value = id }
        }
        loadData()
    }

    fun loadData() {
        _profileState.value = ProfileState.Loading
        viewModelScope.launch {
            // 1. Fetch latest profile info from API
            val profileResult: Result<UserProfileResponse> = networkClient.get("api/v1/users/profile")
            profileResult.fold(
                onSuccess = { profile ->
                    sessionManager.saveUserProfile(
                        firstName = profile.firstName,
                        lastName = profile.lastName,
                        email = profile.email
                    )
                },
                onFailure = {}
            )

            // 2. Fetch models
            val modelsResult: Result<List<AiModelResponse>> = networkClient.get("api/v1/models")
            modelsResult.fold(
                onSuccess = { modelList ->
                    _models.value = modelList
                    
                    val settingsResult: Result<UserSettingsResponse> = networkClient.get("api/v1/users/settings")
                    settingsResult.fold(
                        onSuccess = { settings ->
                            _defaultModelId.value = settings.defaultModelId
                            sessionManager.saveDefaultModel(settings.defaultModelId)
                            _profileState.value = ProfileState.Idle
                        },
                        onFailure = {
                            _profileState.value = ProfileState.Idle
                        }
                    )
                },
                onFailure = { error ->
                    _profileState.value = ProfileState.Error(error.message ?: "Failed to load models")
                }
            )
        }
    }

    fun updateProfile(firstName: String, lastName: String?, onComplete: (Boolean) -> Unit) {
        if (firstName.isBlank()) {
            _profileState.value = ProfileState.Error("First name cannot be empty")
            onComplete(false)
            return
        }

        _profileState.value = ProfileState.Loading
        viewModelScope.launch {
            val request = UpdateProfileRequest(firstName = firstName, lastName = lastName)
            val result: Result<UserProfileResponse> = networkClient.put("api/v1/users/profile", request)
            result.fold(
                onSuccess = { profile ->
                    sessionManager.saveUserProfile(
                        firstName = profile.firstName,
                        lastName = profile.lastName,
                        email = profile.email
                    )
                    _profileState.value = ProfileState.Idle
                    onComplete(true)
                },
                onFailure = { error ->
                    _profileState.value = ProfileState.Error(error.message ?: "Failed to update profile")
                    onComplete(false)
                }
            )
        }
    }

    fun toggleTheme(isDark: Boolean) {
        viewModelScope.launch {
            sessionManager.saveThemeSetting(isDark)
        }
    }

    fun resetState() {
        _profileState.value = ProfileState.Idle
    }

    fun addModel(name: String, apiKey: String) {
        if (name.isBlank() || apiKey.isBlank()) return
        
        _profileState.value = ProfileState.Loading
        viewModelScope.launch {
            val userId = sessionManager.userId.firstOrNull() ?: ""
            val request = CreateAiModelRequest(userId = userId, name = name, apiKey = apiKey)
            val result: Result<AiModelResponse> = networkClient.post("api/v1/models", request)
            
            result.fold(
                onSuccess = {
                    loadData()
                },
                onFailure = { error ->
                    _profileState.value = ProfileState.Error(error.message ?: "Failed to add model config")
                }
            )
        }
    }

    fun setDefaultModel(modelId: String) {
        _profileState.value = ProfileState.Loading
        viewModelScope.launch {
            val request = UpdateUserSettingsRequest(defaultModelId = modelId)
            val result: Result<UserSettingsResponse> = networkClient.put("api/v1/users/settings", request)
            
            result.fold(
                onSuccess = { settings ->
                    sessionManager.saveDefaultModel(settings.defaultModelId)
                    _defaultModelId.value = settings.defaultModelId
                    _profileState.value = ProfileState.Idle
                },
                onFailure = { error ->
                    _profileState.value = ProfileState.Error(error.message ?: "Failed to update default model")
                }
            )
        }
    }

    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            sessionManager.clearSession()
            onLogoutComplete()
        }
    }
}

sealed interface ProfileState {
    object Idle : ProfileState
    object Loading : ProfileState
    data class Error(val message: String) : ProfileState
}
