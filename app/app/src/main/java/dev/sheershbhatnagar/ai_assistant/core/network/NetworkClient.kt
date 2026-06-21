package dev.sheershbhatnagar.ai_assistant.core.network

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json

class NetworkClient(val sessionManager: SessionManager) {

    val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun getBaseUrl(): String {
        val savedHost = sessionManager.hostAddress.firstOrNull()
        val host = if (savedHost.isNullOrBlank()) "http://10.0.2.2:8080" else savedHost
        return if (host.endsWith("/")) host else "$host/"
    }

    suspend inline fun <reified R> get(path: String): Result<R> = runCatching {
        val baseUrl = getBaseUrl()
        val token = sessionManager.jwtToken.firstOrNull()
        val response = client.get(baseUrl + path) {
            if (!token.isNullOrBlank()) {
                header(HttpHeaders.Authorization, "Bearer $token")
            }
            contentType(ContentType.Application.Json)
        }
        response.body<R>()
    }

    suspend inline fun <reified T, reified R> post(path: String, body: T): Result<R> = runCatching {
        val baseUrl = getBaseUrl()
        val token = sessionManager.jwtToken.firstOrNull()
        val response = client.post(baseUrl + path) {
            if (!token.isNullOrBlank()) {
                header(HttpHeaders.Authorization, "Bearer $token")
            }
            contentType(ContentType.Application.Json)
            setBody(body)
        }
        response.body<R>()
    }

    suspend inline fun <reified T, reified R> put(path: String, body: T): Result<R> = runCatching {
        val baseUrl = getBaseUrl()
        val token = sessionManager.jwtToken.firstOrNull()
        val response = client.put(baseUrl + path) {
            if (!token.isNullOrBlank()) {
                header(HttpHeaders.Authorization, "Bearer $token")
            }
            contentType(ContentType.Application.Json)
            setBody(body)
        }
        response.body<R>()
    }
}
