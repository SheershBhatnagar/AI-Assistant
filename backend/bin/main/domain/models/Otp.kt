/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 06/05/26 23:42
 */

package dev.sheershbhatnagar.ai_assistant.domain.models

import java.time.LocalDateTime
import java.util.UUID

data class Otp(
    val id: UUID,
    val userId: UUID,
    val otp: String,
    val expiresAt: LocalDateTime,
    val isUsed: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
