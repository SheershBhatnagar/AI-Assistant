/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 27/05/26 21:03
 */

package dev.sheershbhatnagar.ai_assistant.domain.models

import java.time.LocalDateTime
import java.util.UUID

data class UserSettings(
    val id: UUID,
    val userId: UUID,
    val defaultModelId: UUID,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
