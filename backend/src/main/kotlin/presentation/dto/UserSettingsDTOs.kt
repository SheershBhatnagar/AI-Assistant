/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 27/05/26 21:09
 */

package dev.sheershbhatnagar.ai_assistant.presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserSettingsRequest(
    val defaultModelId: String
)

@Serializable
data class UserSettingsResponse(
    val id: String,
    val userId: String,
    val defaultModelId: String,
    val createdAt: String,
    val updatedAt: String
)
