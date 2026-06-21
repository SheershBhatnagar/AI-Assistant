/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 24/04/26 18:18
 */

package dev.sheershbhatnagar.ai_assistant.plugins

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val httpClientModule = module {
    single {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                })
            }
            install(Logging) {
                // This will now correctly use Ktor's LogLevel
                level = LogLevel.INFO
            }
        }
    }
}
