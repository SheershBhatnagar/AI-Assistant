/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 24/04/26 17:53
 */

package dev.sheershbhatnagar.ai_assistant.infrastructure.database.entities

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object LogsTable : Table("system.logs") {
    val createdAt = datetime("_created_at")
    val id = uuid("id").autoGenerate()
    val userId = reference("user_id", UsersTable.id)
    val action = varchar("action", 255)
    val details = text("details")

    override val primaryKey = PrimaryKey(id)
}
