/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 27/05/26 21:05
 */

package dev.sheershbhatnagar.ai_assistant.infrastructure.database.repositories

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID

import dev.sheershbhatnagar.ai_assistant.domain.models.UserSettings
import dev.sheershbhatnagar.ai_assistant.domain.repository.UserSettingsRepository
import dev.sheershbhatnagar.ai_assistant.infrastructure.database.entities.UserSettingsTable

class UserSettingsRepositoryImpl : UserSettingsRepository {

    private fun rowToUserSettings(row: ResultRow) = UserSettings(
        id = row[UserSettingsTable.id],
        userId = row[UserSettingsTable.userId],
        defaultModelId = row[UserSettingsTable.defaultModelId],
        createdAt = row[UserSettingsTable.createdAt],
        updatedAt = row[UserSettingsTable.updatedAt]
    )

    override suspend fun getSettingsByUserId(userId: UUID): UserSettings? {
        return newSuspendedTransaction {
            UserSettingsTable.selectAll().where { UserSettingsTable.userId eq userId }
                .singleOrNull()?.let(::rowToUserSettings)
        }
    }

    override suspend fun upsertSettings(settings: UserSettings): UserSettings? {
        return newSuspendedTransaction {
            val exists = UserSettingsTable.selectAll().where { UserSettingsTable.userId eq settings.userId }.any()
            if (exists) {
                UserSettingsTable.update({ UserSettingsTable.userId eq settings.userId }) {
                    it[defaultModelId] = settings.defaultModelId
                    it[updatedAt] = settings.updatedAt
                }
            } else {
                UserSettingsTable.insert {
                    it[id] = settings.id
                    it[userId] = settings.userId
                    it[defaultModelId] = settings.defaultModelId
                    it[createdAt] = settings.createdAt
                    it[updatedAt] = settings.updatedAt
                }
            }
            UserSettingsTable.selectAll().where { UserSettingsTable.userId eq settings.userId }
                .singleOrNull()?.let(::rowToUserSettings)
        }
    }
}
