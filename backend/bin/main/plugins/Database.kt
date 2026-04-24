package dev.sheershbhatnagar.ai_assistant.plugins

import io.ktor.server.application.*

import dev.sheershbhatnagar.ai_assistant.infrastructure.database.DatabaseFactory

fun Application.configureDatabases() {
    DatabaseFactory.init()

    log.info("Database connection successfully established!")
}
