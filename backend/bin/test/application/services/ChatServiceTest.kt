/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 27/05/26 21:14
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

import dev.sheershbhatnagar.ai_assistant.domain.models.*
import dev.sheershbhatnagar.ai_assistant.domain.repository.ChatRepository
import dev.sheershbhatnagar.ai_assistant.domain.repository.UserSettingsRepository
import dev.sheershbhatnagar.ai_assistant.infrastructure.external.AiClient

class ChatServiceTest {

    private val mockChatRepository = mockk<ChatRepository>()
    private val mockAiModelService = mockk<AiModelService>()
    private val mockAiClient = mockk<AiClient>()
    private val mockUserSettingsRepository = mockk<UserSettingsRepository>()

    private val chatService = ChatService(
        mockChatRepository,
        mockAiModelService,
        mockAiClient,
        mockUserSettingsRepository
    )

    @Test
    fun `processUserMessage should resolve default model when modelId is null`() = runTest {
        val userId = UUID.randomUUID()
        val conversationId = UUID.randomUUID()
        val defaultModelId = UUID.randomUUID()

        val incomingMessage = Message(
            id = UUID.randomUUID(),
            userId = userId,
            conversationId = conversationId,
            modelId = null, // Null modelId, trigger fallback
            content = "What is Kotlin?",
            senderType = SenderType.user,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val userSettings = UserSettings(
            id = UUID.randomUUID(),
            userId = userId,
            defaultModelId = defaultModelId,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val mockModelDetails = AiModel(
            id = defaultModelId,
            userId = userId,
            name = "gemini-1.5-flash",
            apiKey = "api-key-xyz",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        coEvery { mockUserSettingsRepository.getSettingsByUserId(userId) } returns userSettings
        coEvery { mockChatRepository.createMessage(any()) } returnsArgument 0
        coEvery { mockAiModelService.getModelDetails(defaultModelId) } returns mockModelDetails
        coEvery { mockAiClient.callGemini("gemini-1.5-flash", "api-key-xyz", "What is Kotlin?") } returns "Kotlin is awesome!"

        val result = chatService.processUserMessage(incomingMessage)

        assertNotNull(result)
        assertEquals(SenderType.ai, result?.senderType)
        assertEquals("Kotlin is awesome!", result?.content)
        assertEquals(defaultModelId, result?.modelId)

        // Verify we saved both the user message (with resolved modelId) and the AI response
        coVerify(exactly = 2) { mockChatRepository.createMessage(any()) }
    }

    @Test
    fun `processUserMessage should throw exception when modelId is null and user has no default settings`() = runTest {
        val userId = UUID.randomUUID()
        val conversationId = UUID.randomUUID()

        val incomingMessage = Message(
            id = UUID.randomUUID(),
            userId = userId,
            conversationId = conversationId,
            modelId = null,
            content = "Hi",
            senderType = SenderType.user,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        coEvery { mockUserSettingsRepository.getSettingsByUserId(userId) } returns null

        val exception = assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                chatService.processUserMessage(incomingMessage)
            }
        }

        assertTrue(exception.message!!.contains("No default model configured"))
        coVerify(exactly = 0) { mockChatRepository.createMessage(any()) }
    }
}
