/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 24/04/26 16:04
 */

package dev.sheershbhatnagar.ai_assistant.domain.repository

import java.util.UUID

import dev.sheershbhatnagar.ai_assistant.domain.models.Conversation
import dev.sheershbhatnagar.ai_assistant.domain.models.Message

interface ChatRepository {
    // Conversation Actions
    suspend fun createConversation(conversation: Conversation): Conversation?
    suspend fun getConversationsByUserId(userId: UUID): List<Conversation>

    // Message Actions
    suspend fun createMessage(message: Message): Message?
    suspend fun getMessagesByConversationId(conversationId: UUID): List<Message>
}
