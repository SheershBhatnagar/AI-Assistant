package dev.sheershbhatnagar.ai_assistant.application.services

/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 24/04/26 16:21
 */

import java.util.UUID

import dev.sheershbhatnagar.ai_assistant.domain.models.Conversation
import dev.sheershbhatnagar.ai_assistant.domain.models.Message
import dev.sheershbhatnagar.ai_assistant.domain.repository.ChatRepository

class ChatService(private val chatRepository: ChatRepository) {

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
}
