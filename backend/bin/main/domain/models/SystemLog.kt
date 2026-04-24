package dev.sheershbhatnagar.ai_assistant.domain.models

/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 24/04/26 17:54
 */

import java.time.LocalDateTime
import java.util.UUID

data class SystemLog(
    val id: UUID,
    val userId: UUID,
    val action: String,
    val details: String, // Stringified JSON
    val createdAt: LocalDateTime
)
    