package dev.sheershbhatnagar.ai_assistant.presentation.dto

/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 24/04/26 18:05
 */

import kotlinx.serialization.Serializable

// Incoming Request
@Serializable
data class CreateLogRequest(
    val userId: String,
    val action: String,
    val details: String
)

// Outgoing Response
@Serializable
data class SystemLogResponse(
    val id: String,
    val action: String,
    val details: String,
    val createdAt: String
)
