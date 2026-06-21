/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 24/04/26 16:01
 */

package dev.sheershbhatnagar.ai_assistant.domain.models

import java.time.LocalDateTime
import java.util.UUID

data class Conversation(
    val id: UUID,
    val userId: UUID,
    val title: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
