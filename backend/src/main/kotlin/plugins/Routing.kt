package dev.sheershbhatnagar.ai_assistant.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

import dev.sheershbhatnagar.ai_assistant.application.services.*
import dev.sheershbhatnagar.ai_assistant.presentation.routes.*

fun Application.configureRouting() {

    val authService by inject<AuthService>()
    val aiModelService by inject<AiModelService>()
    val chatService by inject<ChatService>()
    val systemLogService by inject<SystemLogService>()
    val userService by inject<UserService>()
    val userSettingsService by inject<UserSettingsService>()

    routing {
        authRoutes(authService)
        aiModelRoutes(aiModelService)
        chatRoutes(chatService)
        systemLogRoutes(systemLogService)
        userRoutes(userService)
        userSettingsRoutes(userSettingsService)
    }
}
