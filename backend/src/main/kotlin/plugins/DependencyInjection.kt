package dev.sheershbhatnagar.ai_assistant.plugins

import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.logger.slf4jLogger
import org.koin.ktor.plugin.Koin

import dev.sheershbhatnagar.ai_assistant.application.services.*
import dev.sheershbhatnagar.ai_assistant.domain.repository.*
import dev.sheershbhatnagar.ai_assistant.infrastructure.database.repositories.*
import dev.sheershbhatnagar.ai_assistant.infrastructure.external.AiClient
import dev.sheershbhatnagar.ai_assistant.infrastructure.external.EmailClient
import dev.sheershbhatnagar.ai_assistant.infrastructure.external.SmtpEmailClient

val appModule = module {

    single { AuthService(get(), get(), get(), get()) }

    single<OtpRepository> { OtpRepositoryImpl() }

    single { AiClient(get()) }
    single<EmailClient> { SmtpEmailClient() }
    
    // AI Config Domain
    single<AiModelRepository> { AiModelRepositoryImpl() }
    single { AiModelService(get()) }

    // Chat Domain
    single<ChatRepository> { ChatRepositoryImpl() }
    single { ChatService(get(), get(), get(), get()) }

    // System Logs Domain
    single<SystemLogRepository> { SystemLogRepositoryImpl() }
    single { SystemLogService(get()) }

    // User Domain
    single<UserRepository> { UserRepositoryImpl() }
    single { UserService(get()) }
    single<UserSettingsRepository> { UserSettingsRepositoryImpl() }
    single { UserSettingsService(get(), get()) }
}

fun Application.configureDI() {
    install(Koin) {
        slf4jLogger()
        modules(
            appModule,
            httpClientModule
        )
    }
}
