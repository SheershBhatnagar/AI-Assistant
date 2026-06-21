/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 24/04/26 16:21
 */

package dev.sheershbhatnagar.ai_assistant.application.services

import java.time.LocalDateTime
import java.util.UUID

import dev.sheershbhatnagar.ai_assistant.domain.models.Conversation
import dev.sheershbhatnagar.ai_assistant.domain.models.Message
import dev.sheershbhatnagar.ai_assistant.domain.models.SenderType
import dev.sheershbhatnagar.ai_assistant.domain.repository.ChatRepository
import dev.sheershbhatnagar.ai_assistant.domain.repository.UserSettingsRepository
import dev.sheershbhatnagar.ai_assistant.infrastructure.external.AiClient

class ChatService(
    private val chatRepository: ChatRepository,
    private val aiModelService: AiModelService,
    private val aiClient: AiClient,
    private val userSettingsRepository: UserSettingsRepository
) {
    suspend fun startConversation(conversation: Conversation): Conversation? {
        return chatRepository.createConversation(conversation)
    }

    suspend fun getUserConversations(userId: UUID): List<Conversation> {
        return chatRepository.getConversationsByUserId(userId)
    }

    suspend fun addMessage(message: Message): Message? {
        // Business Rule: Ensure messages aren't empty
        if (message.content.isBlank()) {
            throw IllegalArgumentException("Message content cannot be empty.")
        }
        return chatRepository.createMessage(message)
    }

    suspend fun getConversationHistory(conversationId: UUID): List<Message> {
        return chatRepository.getMessagesByConversationId(conversationId)
    }

    suspend fun processUserMessage(message: Message): Message? {
        // 1. Resolve default model if not supplied
        val resolvedModelId = message.modelId ?: run {
            val settings = userSettingsRepository.getSettingsByUserId(message.userId)
                ?: throw IllegalArgumentException("No default model configured, and no model ID was provided in the message request.")
            settings.defaultModelId
        }

        val messageToSave = message.copy(modelId = resolvedModelId)

        // 2. Save user message to DB
        chatRepository.createMessage(messageToSave)

        // 3. Lookup the Model Configuration
        val config = aiModelService.getModelDetails(resolvedModelId)
            ?: throw IllegalArgumentException("Model config not found")

        // 4. Call the correct AI Provider
        val aiResponseContent = when {
            config.name.contains("gemini", ignoreCase = true) ->
                aiClient.callGemini(config.name, config.apiKey, message.content)
            config.name.contains("gpt", ignoreCase = true) ->
                aiClient.callOpenAI(config.name, config.apiKey, message.content)
            else -> "Unknown model provider"
        }

        // 5. Create and Save the AI's response message
        val aiMessage = Message(
            id = UUID.randomUUID(),
            userId = message.userId,
            conversationId = message.conversationId,
            modelId = resolvedModelId,
            content = aiResponseContent,
            senderType = SenderType.ai,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        return chatRepository.createMessage(aiMessage)
    }
}
