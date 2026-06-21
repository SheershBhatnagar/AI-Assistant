/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 24/04/26 17:01
 */

package dev.sheershbhatnagar.ai_assistant.domain.models

import java.time.LocalDateTime
import java.util.UUID

data class AiModel(
    val id: UUID,
    val userId: UUID,
    val name: String,
    val apiKey: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
