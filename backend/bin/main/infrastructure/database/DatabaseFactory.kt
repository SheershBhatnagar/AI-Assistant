package dev.sheershbhatnagar.ai_assistant.infrastructure.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.flywaydb.core.Flyway

object DatabaseFactory {

    fun init() {
        val pool = hikari()

        val flyway = Flyway.configure()
            .dataSource(pool)
            .baselineOnMigrate(true)
            .load()
        flyway.migrate()

        Database.connect(pool)
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig().apply {
            val dbHost = System.getenv("DB_HOST") ?: "localhost"
            val dbPort = System.getenv("DB_PORT") ?: "5432"
            val dbName = System.getenv("DB_NAME") ?: "ai_assistant"
            val dbUser = System.getenv("DB_USER") ?: "postgres"
            val dbPassword = System.getenv("DB_PASSWORD") ?: "postgres"

            driverClassName = "org.postgresql.Driver"
            jdbcUrl = "jdbc:postgresql://$dbHost:$dbPort/$dbName"
            username = dbUser
            password = dbPassword

            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        return HikariDataSource(config)
    }
}
