/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 24/04/26 16:58
 */

package dev.sheershbhatnagar.ai_assistant.infrastructure.database.entities

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object ModelsTable : Table("ai_config.models") {
    val createdAt = datetime("_created_at")
    val updatedAt = datetime("_updated_at")
    val id = uuid("id").autoGenerate()
    val userId = reference("user_id", UsersTable.id)
    val name = varchar("name", 100)
    val apiKey = varchar("api_key", 255)

    override val primaryKey = PrimaryKey(id)
}
