package dev.sheershbhatnagar.ai_assistant.infrastructure.database.entities

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object UsersTable : Table("users.accounts") {
    val createdAt = datetime("_created_at")
    val updatedAt = datetime("_updated_at")
    val id = uuid("id").autoGenerate()
    val email = varchar("email", 255).uniqueIndex()
    val firstName = varchar("first_name", 100)
    val middleName = varchar("middle_name", 100).nullable()
    val lastName = varchar("last_name", 100).nullable()

    override val primaryKey = PrimaryKey(id)
}
