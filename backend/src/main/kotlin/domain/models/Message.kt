/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 24/04/26 16:02
 */

package dev.sheershbhatnagar.ai_assistant.domain.models

import java.time.LocalDateTime
import java.util.UUID

data class Message(
    val id: UUID,
    val userId: UUID,
    val conversationId: UUID,
    val modelId: UUID?,
    val content: String,
    val senderType: SenderType,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
