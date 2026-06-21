package dev.sheershbhatnagar.ai_assistant.features.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.sheershbhatnagar.ai_assistant.core.network.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(application)
    private val networkClient = NetworkClient(sessionManager)

    private val _userName = MutableStateFlow("User")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _homeState = MutableStateFlow<HomeState>(HomeState.Idle)
    val homeState: StateFlow<HomeState> = _homeState.asStateFlow()

    private val _defaultModelName = MutableStateFlow("Default Model")
    val defaultModelName: StateFlow<String> = _defaultModelName.asStateFlow()

    init {
        viewModelScope.launch {
            sessionManager.userFirstName.collect { name ->
                if (!name.isNullOrBlank()) {
                    _userName.value = name
                }
            }
        }
        loadDefaultModelName()
    }

    fun loadDefaultModelName() {
        viewModelScope.launch {
            val defaultModelId = sessionManager.defaultModelId.firstOrNull()
            if (!defaultModelId.isNullOrBlank()) {
                val result: Result<List<AiModelResponse>> = networkClient.get("api/v1/models")
                result.fold(
                    onSuccess = { models ->
                        val matchingModel = models.firstOrNull { it.id == defaultModelId }
                        if (matchingModel != null) {
                            _defaultModelName.value = matchingModel.name
                        }
                    },
                    onFailure = {}
                )
            }
        }
    }

    fun startConversationFromPrompt(prompt: String, onNavigateToChat: (String) -> Unit) {
        if (prompt.trim().isBlank()) return

        _homeState.value = HomeState.Loading
        viewModelScope.launch {
            val title = if (prompt.length > 25) prompt.take(22) + "..." else prompt

            val conversationResult: Result<ConversationResponse> = networkClient.post(
                "api/v1/chat/conversations",
                CreateConversationRequest(title = title)
            )

            conversationResult.fold(
                onSuccess = { conversation ->
                    val defaultModelId = sessionManager.defaultModelId.firstOrNull()
                    val messageRequest = SendMessageRequest(
                        conversationId = conversation.id,
                        modelId = defaultModelId,
                        content = prompt,
                        senderType = "user"
                    )

                    val messageResult: Result<MessageResponse> = networkClient.post(
                        "api/v1/chat/messages",
                        messageRequest
                    )

                    messageResult.fold(
                        onSuccess = {
                            _homeState.value = HomeState.Idle
                            onNavigateToChat(conversation.id)
                        },
                        onFailure = { error ->
                            _homeState.value = HomeState.Error(error.message ?: "Failed to send initial message")
                        }
                    )
                },
                onFailure = { error ->
                    _homeState.value = HomeState.Error(error.message ?: "Failed to create conversation")
                }
            )
        }
    }
}

sealed interface HomeState {
    object Idle : HomeState
    object Loading : HomeState
    data class Error(val message: String) : HomeState
}
