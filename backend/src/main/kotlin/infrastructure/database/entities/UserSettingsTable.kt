/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 27/05/26 21:02
 */

package dev.sheershbhatnagar.ai_assistant.infrastructure.database.entities

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object UserSettingsTable : Table("users.user_settings") {
    val createdAt = datetime("_created_at")
    val updatedAt = datetime("_updated_at")
    val id = uuid("id")
    val userId = reference("user_id", UsersTable.id).uniqueIndex()
    val defaultModelId = reference("default_model_id", ModelsTable.id)

    override val primaryKey = PrimaryKey(id)
}
