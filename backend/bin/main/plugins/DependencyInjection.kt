package dev.sheershbhatnagar.ai_assistant.plugins

import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.logger.slf4jLogger
import org.koin.ktor.plugin.Koin

import dev.sheershbhatnagar.ai_assistant.application.services.*
import dev.sheershbhatnagar.ai_assistant.domain.repository.*
import dev.sheershbhatnagar.ai_assistant.infrastructure.database.repositories.*

val appModule = module {
    // AI Config Domain
    single<AiModelRepository> { AiModelRepositoryImpl() }
    single { AiModelService(get()) }

    // Chat Domain
    single<ChatRepository> { ChatRepositoryImpl() }
    single { ChatService(get()) }

    // System Logs Domain
    single<SystemLogRepository> { SystemLogRepositoryImpl() }
    single { SystemLogService(get()) }

    // User Domain
    single<UserRepository> { UserRepositoryImpl() }
    single { UserService(get()) }
}

fun Application.configureDI() {
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
}
