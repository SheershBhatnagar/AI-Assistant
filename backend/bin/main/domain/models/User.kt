package dev.sheershbhatnagar.ai_assistant.domain.models

import java.time.LocalDateTime
import java.util.UUID

data class User(
    val id: UUID,
    val email: String,
    val firstName: String,
    val middleName: String? = null,
    val lastName: String? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
