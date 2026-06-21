package dev.sheershbhatnagar.ai_assistant.core.network

import kotlinx.serialization.Serializable

@Serializable
data class CreateConversationRequest(
    val title: String
)

@Serializable
data class ConversationResponse(
    val id: String,
    val title: String?,
    val createdAt: String
)

@Serializable
data class SendMessageRequest(
    val conversationId: String,
    val modelId: String? = null,
    val content: String,
    val senderType: String // "user" or "ai"
)

@Serializable
data class MessageResponse(
    val id: String,
    val senderType: String,
    val content: String,
    val createdAt: String
)

@Serializable
data class CreateAiModelRequest(
    val userId: String,
    val name: String,
    val apiKey: String
)

@Serializable
data class AiModelResponse(
    val id: String,
    val name: String,
    val createdAt: String
)

@Serializable
data class UpdateUserSettingsRequest(
    val defaultModelId: String
)

@Serializable
data class UserSettingsResponse(
    val id: String,
    val userId: String,
    val defaultModelId: String,
    val createdAt: String,
    val updatedAt: String
)
