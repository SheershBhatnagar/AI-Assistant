/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 27/05/26 21:12
 */

package dev.sheershbhatnagar.ai_assistant.application.services

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

import dev.sheershbhatnagar.ai_assistant.domain.models.AiModel
import dev.sheershbhatnagar.ai_assistant.domain.models.UserSettings
import dev.sheershbhatnagar.ai_assistant.domain.repository.AiModelRepository
import dev.sheershbhatnagar.ai_assistant.domain.repository.UserSettingsRepository

class UserSettingsServiceTest {

    private val mockSettingsRepository = mockk<UserSettingsRepository>()
    private val mockModelRepository = mockk<AiModelRepository>()
    private val userSettingsService = UserSettingsService(mockSettingsRepository, mockModelRepository)

    @Test
    fun `getUserSettings should return settings if they exist`() = runTest {
        val userId = UUID.randomUUID()
        val expectedSettings = UserSettings(
            id = UUID.randomUUID(),
            userId = userId,
            defaultModelId = UUID.randomUUID(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        coEvery { mockSettingsRepository.getSettingsByUserId(userId) } returns expectedSettings

        val result = userSettingsService.getUserSettings(userId)

        assertNotNull(result)
        assertEquals(expectedSettings.id, result?.id)
        assertEquals(expectedSettings.defaultModelId, result?.defaultModelId)
    }

    @Test
    fun `updateDefaultModel should successfully save default model when it exists and belongs to user`() = runTest {
        val userId = UUID.randomUUID()
        val modelId = UUID.randomUUID()
        val existingModel = AiModel(
            id = modelId,
            userId = userId,
            name = "gemini-1.5-flash",
            apiKey = "key-123",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val targetSettings = UserSettings(
            id = UUID.randomUUID(),
            userId = userId,
            defaultModelId = modelId,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        coEvery { mockModelRepository.getModelById(modelId) } returns existingModel
        coEvery { mockSettingsRepository.getSettingsByUserId(userId) } returns null
        coEvery { mockSettingsRepository.upsertSettings(any()) } returns targetSettings

        val result = userSettingsService.updateDefaultModel(userId, modelId)

        assertNotNull(result)
        assertEquals(modelId, result.defaultModelId)
        coVerify(exactly = 1) { mockSettingsRepository.upsertSettings(any()) }
    }

    @Test
    fun `updateDefaultModel should throw exception when model configuration does not exist`() = runTest {
        val userId = UUID.randomUUID()
        val modelId = UUID.randomUUID()

        coEvery { mockModelRepository.getModelById(modelId) } returns null

        val exception = assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                userSettingsService.updateDefaultModel(userId, modelId)
            }
        }

        assertEquals("Model configuration not found", exception.message)
        coVerify(exactly = 0) { mockSettingsRepository.upsertSettings(any()) }
    }

    @Test
    fun `updateDefaultModel should throw exception when model exists but belongs to a different user`() = runTest {
        val userId = UUID.randomUUID()
        val strangerId = UUID.randomUUID()
        val modelId = UUID.randomUUID()
        val strangerModel = AiModel(
            id = modelId,
            userId = strangerId,
            name = "gemini-1.5-flash",
            apiKey = "key-123",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        coEvery { mockModelRepository.getModelById(modelId) } returns strangerModel

        val exception = assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                userSettingsService.updateDefaultModel(userId, modelId)
            }
        }

        assertEquals("Model configuration does not belong to the user", exception.message)
        coVerify(exactly = 0) { mockSettingsRepository.upsertSettings(any()) }
    }
}
