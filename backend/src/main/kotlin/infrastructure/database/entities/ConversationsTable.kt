package dev.sheershbhatnagar.ai_assistant.infrastructure.database.entities

/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 24/04/26 15:55
 */

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object ConversationsTable : Table("chat.conversations") {
    val createdAt = datetime("_created_at")
    val updatedAt = datetime("_updated_at")
    val id = uuid("id").autoGenerate()
    val userId = reference("user_id", UsersTable.id)
    val title = varchar("title", 100).nullable()

    override val primaryKey = PrimaryKey(id)
}
