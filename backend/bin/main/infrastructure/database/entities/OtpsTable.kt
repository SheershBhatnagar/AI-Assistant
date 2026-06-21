/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 06/05/26 23:41
 */

package dev.sheershbhatnagar.ai_assistant.infrastructure.database.entities

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object OtpsTable : Table("users.otps") {
    val id = uuid("id").autoGenerate()
    val userId = reference("user_id", UsersTable.id)
    val otp = varchar("otp", 10)
    val expiresAt = datetime("expires_at")
    val isUsed = bool("is_used").default(false)

    val createdAt = datetime("_created_at")
    val updatedAt = datetime("_updated_at")

    override val primaryKey = PrimaryKey(id)
}
    