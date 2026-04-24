package dev.sheershbhatnagar.ai_assistant

import io.ktor.server.netty.*

import dev.sheershbhatnagar.ai_assistant.plugins.*
import io.ktor.server.application.Application

fun main(args: Array<String>): Unit = EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    configureDI()
    configureSerialization()
    configureDatabases()
    configureRouting()
}
