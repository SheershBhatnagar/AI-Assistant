package dev.sheershbhatnagar.ai_assistant

import io.ktor.server.netty.*
import io.ktor.server.application.Application

import dev.sheershbhatnagar.ai_assistant.plugins.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    configureDI()
    configureSerialization()
    configureSecurity()
    configureStatusPages()
    configureDatabases()
    configureRouting()
}
