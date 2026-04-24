package dev.sheershbhatnagar.ai_assistant.infrastructure.database.repositories

/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 24/04/26 16:06
 */

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID

import dev.sheershbhatnagar.ai_assistant.domain.models.Conversation
import dev.sheershbhatnagar.ai_assistant.domain.models.Message
import dev.sheershbhatnagar.ai_assistant.domain.repository.ChatRepository
import dev.sheershbhatnagar.ai_assistant.infrastructure.database.entities.ConversationsTable
import dev.sheershbhatnagar.ai_assistant.infrastructure.database.entities.MessagesTable

class ChatRepositoryImpl : ChatRepository {

    // --- Mappers ---
    private fun rowToConversation(row: ResultRow) = Conversation(
        id = row[ConversationsTable.id],
        userId = row[ConversationsTable.userId],
        title = row[ConversationsTable.title],
        createdAt = row[ConversationsTable.createdAt],
        updatedAt = row[ConversationsTable.updatedAt]
    )

    private fun rowToMessage(row: ResultRow) = Message(
        id = row[MessagesTable.id],
        userId = row[MessagesTable.userId],
        conversationId = row[MessagesTable.conversationId],
        modelId = row[MessagesTable.modelId],
        content = row[MessagesTable.content],
        senderType = row[MessagesTable.senderType],
        createdAt = row[MessagesTable.createdAt],
        updatedAt = row[MessagesTable.updatedAt]
    )

    // --- Conversation Implementations ---
    override suspend fun createConversation(conversation: Conversation): Conversation? {
        return newSuspendedTransaction {
            val statement = ConversationsTable.insert {
                it[id] = conversation.id
                it[userId] = conversation.userId
                it[title] = conversation.title
                it[createdAt] = conversation.createdAt
                it[updatedAt] = conversation.updatedAt
            }
            statement.resultedValues?.singleOrNull()?.let(::rowToConversation)
        }
    }

    override suspend fun getConversationsByUserId(userId: UUID): List<Conversation> {
        return newSuspendedTransaction {
            ConversationsTable.select { ConversationsTable.userId eq userId }
                .orderBy(ConversationsTable.updatedAt to SortOrder.DESC) // Latest first
                .map(::rowToConversation)
        }
    }

    // --- Message Implementations ---
    override suspend fun createMessage(message: Message): Message? {
        return newSuspendedTransaction {
            val statement = MessagesTable.insert {
                it[id] = message.id
                it[userId] = message.userId
                it[conversationId] = message.conversationId
                it[modelId] = message.modelId
                it[content] = message.content
                it[senderType] = message.senderType
                it[createdAt] = message.createdAt
                it[updatedAt] = message.updatedAt
            }
            statement.resultedValues?.singleOrNull()?.let(::rowToMessage)
        }
    }

    override suspend fun getMessagesByConversationId(conversationId: UUID): List<Message> {
        return newSuspendedTransaction {
            MessagesTable.select { MessagesTable.conversationId eq conversationId }
                .orderBy(MessagesTable.createdAt to SortOrder.ASC) // Chronological order
                .map(::rowToMessage)
        }
    }
}
