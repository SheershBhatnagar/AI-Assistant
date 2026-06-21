/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 24/04/26 17:16
 */

package dev.sheershbhatnagar.ai_assistant.presentation.dto

import kotlinx.serialization.Serializable

// Incoming Request (Contains the sensitive API Key)
@Serializable
data class CreateAiModelRequest(
    val userId: String,
    val name: String,
    val apiKey: String
)

// Outgoing Response (Safe! The API Key is intentionally omitted)
@Serializable
data class AiModelResponse(
    val id: String,
    val name: String,
    val createdAt: String
)
    