package dev.sheershbhatnagar.ai_assistant.features.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.sheershbhatnagar.ai_assistant.core.network.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(application)
    private val networkClient = NetworkClient(sessionManager)

    private val _messages = MutableStateFlow<List<MessageResponse>>(emptyList())
    val messages: StateFlow<List<MessageResponse>> = _messages.asStateFlow()

    private val _chatState = MutableStateFlow<ChatState>(ChatState.Idle)
    val chatState: StateFlow<ChatState> = _chatState.asStateFlow()

    fun loadMessages(conversationId: String) {
        _chatState.value = ChatState.Loading
        viewModelScope.launch {
            val result: Result<List<MessageResponse>> = networkClient.get("api/v1/chat/conversations/$conversationId/messages")
            result.fold(
                onSuccess = { history ->
                    _messages.value = history
                    _chatState.value = ChatState.Idle
                },
                onFailure = { error ->
                    _chatState.value = ChatState.Error(error.message ?: "Failed to load chat history")
                }
            )
        }
    }

    fun sendMessage(conversationId: String, content: String) {
        if (content.trim().isBlank()) return

        val tempUserMessage = MessageResponse(
            id = UUID.randomUUID().toString(),
            senderType = "user",
            content = content,
            createdAt = Date().toString()
        )
        _messages.value = _messages.value + tempUserMessage

        _chatState.value = ChatState.Sending
        viewModelScope.launch {
            val defaultModelId = sessionManager.defaultModelId.firstOrNull()
            val request = SendMessageRequest(
                conversationId = conversationId,
                modelId = defaultModelId,
                content = content,
                senderType = "user"
            )

            val result: Result<MessageResponse> = networkClient.post("api/v1/chat/messages", request)
            result.fold(
                onSuccess = {
                    loadMessages(conversationId)
                },
                onFailure = { error ->
                    _chatState.value = ChatState.Error(error.message ?: "Failed to send message")
                }
            )
        }
    }
}

sealed interface ChatState {
    object Idle : ChatState
    object Loading : ChatState
    object Sending : ChatState
    data class Error(val message: String) : ChatState
}
