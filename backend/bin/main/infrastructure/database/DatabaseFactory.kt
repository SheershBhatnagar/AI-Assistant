package dev.sheershbhatnagar.ai_assistant.infrastructure.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

import dev.sheershbhatnagar.ai_assistant.infrastructure.database.entities.*

object DatabaseFactory {

    fun init() {
        val pool = hikari()

        Database.connect(pool)

        transaction {
            SchemaUtils.create(ConversationsTable)
            SchemaUtils.create(MessagesTable)
            SchemaUtils.create(ModelsTable)
            SchemaUtils.create(LogsTable)
            SchemaUtils.create(UsersTable)
        }
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = "jdbc:postgresql://192.168.1.10:5432/ai_assistant"
            username = "postgres"
            password = "postgres"

            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        return HikariDataSource(config)
    }
}
