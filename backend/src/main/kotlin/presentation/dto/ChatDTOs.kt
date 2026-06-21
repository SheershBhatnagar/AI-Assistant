/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 24/04/26 16:28
 */

package dev.sheershbhatnagar.ai_assistant.presentation.dto

import kotlinx.serialization.Serializable

import dev.sheershbhatnagar.ai_assistant.domain.models.SenderType

// --- Requests (Incoming JSON) ---
@Serializable
data class CreateConversationRequest(
    val title: String
)

@Serializable
data class SendMessageRequest(
    val conversationId: String,
    val modelId: String? = null,
    val content: String,
    val senderType: SenderType
)

// --- Responses (Outgoing JSON) ---
@Serializable
data class ConversationResponse(
    val id: String,
    val title: String?,
    val createdAt: String
)

@Serializable
data class MessageResponse(
    val id: String,
    val senderType: SenderType,
    val content: String,
    val createdAt: String
)
