package dev.sheershbhatnagar.ai_assistant.features.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.sheershbhatnagar.ai_assistant.core.network.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(application)
    private val networkClient = NetworkClient(sessionManager)

    private val _conversations = MutableStateFlow<List<ConversationResponse>>(emptyList())
    val conversations: StateFlow<List<ConversationResponse>> = _conversations.asStateFlow()

    private val _historyState = MutableStateFlow<HistoryState>(HistoryState.Idle)
    val historyState: StateFlow<HistoryState> = _historyState.asStateFlow()

    fun loadConversations() {
        _historyState.value = HistoryState.Loading
        viewModelScope.launch {
            val result: Result<List<ConversationResponse>> = networkClient.get("api/v1/chat/conversations")
            result.fold(
                onSuccess = { history ->
                    _conversations.value = history
                    _historyState.value = HistoryState.Idle
                },
                onFailure = { error ->
                    _historyState.value = HistoryState.Error(error.message ?: "Failed to load chat history")
                }
            )
        }
    }
}

sealed interface HistoryState {
    object Idle : HistoryState
    object Loading : HistoryState
    data class Error(val message: String) : HistoryState
}
