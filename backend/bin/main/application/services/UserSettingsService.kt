/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 27/05/26 21:07
 */

package dev.sheershbhatnagar.ai_assistant.application.services

import java.time.LocalDateTime
import java.util.UUID

import dev.sheershbhatnagar.ai_assistant.domain.models.UserSettings
import dev.sheershbhatnagar.ai_assistant.domain.repository.AiModelRepository
import dev.sheershbhatnagar.ai_assistant.domain.repository.UserSettingsRepository

class UserSettingsService(
    private val userSettingsRepository: UserSettingsRepository,
    private val aiModelRepository: AiModelRepository
) {
    suspend fun getUserSettings(userId: UUID): UserSettings? {
        return userSettingsRepository.getSettingsByUserId(userId)
    }

    suspend fun updateDefaultModel(userId: UUID, defaultModelId: UUID): UserSettings {
        // Security check: Verify the model configuration exists and belongs to the requesting user
        val model = aiModelRepository.getModelById(defaultModelId)
            ?: throw IllegalArgumentException("Model configuration not found")

        if (model.userId != userId) {
            throw IllegalArgumentException("Model configuration does not belong to the user")
        }

        val existingSettings = userSettingsRepository.getSettingsByUserId(userId)
        val now = LocalDateTime.now()

        val settingsToSave = if (existingSettings != null) {
            existingSettings.copy(
                defaultModelId = defaultModelId,
                updatedAt = now
            )
        } else {
            UserSettings(
                id = UUID.randomUUID(),
                userId = userId,
                defaultModelId = defaultModelId,
                createdAt = now,
                updatedAt = now
            )
        }

        return userSettingsRepository.upsertSettings(settingsToSave)
            ?: throw IllegalStateException("Failed to update user settings")
    }
}
