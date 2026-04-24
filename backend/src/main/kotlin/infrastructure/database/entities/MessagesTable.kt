package dev.sheershbhatnagar.ai_assistant.infrastructure.database.entities

/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 24/04/26 15:57
 */

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

import dev.sheershbhatnagar.ai_assistant.domain.models.SenderType

object MessagesTable : Table("chat.messages") { 
    val createdAt = datetime("_created_at")
    val updatedAt = datetime("_updated_at")
    val id = uuid("id").autoGenerate()
    val userId = reference("user_id", UsersTable.id)
    val conversationId = reference("conversation_id", ConversationsTable.id)
    val modelId = uuid("model_id")
    val content = text("content")
    val senderType = enumerationByName("sender_type", 50, SenderType::class)

    override val primaryKey = PrimaryKey(id)
}
