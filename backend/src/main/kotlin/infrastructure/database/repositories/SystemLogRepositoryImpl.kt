/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 24/04/26 17:55
 */

package dev.sheershbhatnagar.ai_assistant.infrastructure.database.repositories

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID

import dev.sheershbhatnagar.ai_assistant.domain.models.SystemLog
import dev.sheershbhatnagar.ai_assistant.domain.repository.SystemLogRepository
import dev.sheershbhatnagar.ai_assistant.infrastructure.database.entities.LogsTable

class SystemLogRepositoryImpl : SystemLogRepository {

    private fun rowToSystemLog(row: ResultRow) = SystemLog(
        id = row[LogsTable.id],
        userId = row[LogsTable.userId],
        action = row[LogsTable.action],
        details = row[LogsTable.details],
        createdAt = row[LogsTable.createdAt]
    )

    override suspend fun createLog(log: SystemLog): SystemLog? {
        return newSuspendedTransaction {
            val statement = LogsTable.insert {
                it[id] = log.id
                it[userId] = log.userId
                it[action] = log.action
                it[details] = log.details
                it[createdAt] = log.createdAt
            }
            statement.resultedValues?.singleOrNull()?.let(::rowToSystemLog)
        }
    }

    override suspend fun getLogsByUserId(userId: UUID): List<SystemLog> {
        return newSuspendedTransaction {
            LogsTable.select { LogsTable.userId eq userId }
                .orderBy(LogsTable.createdAt to SortOrder.DESC) // Newest logs first
                .map(::rowToSystemLog)
        }
    }
}
    