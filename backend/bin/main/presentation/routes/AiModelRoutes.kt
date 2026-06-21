package dev.sheershbhatnagar.ai_assistant.presentation.routes

import io.ktor.http.*
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDateTime
import java.util.UUID

import dev.sheershbhatnagar.ai_assistant.application.services.AiModelService
import dev.sheershbhatnagar.ai_assistant.domain.models.AiModel
import dev.sheershbhatnagar.ai_assistant.presentation.dto.*

fun Route.aiModelRoutes(aiModelService: AiModelService) {
    route("/api/v1/models") {
        authenticate("auth-jwt") {
            // 1. Add a new AI Model configuration
            post {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val secureUserId = principal?.payload?.getClaim("userId")?.asString()
                        ?: throw IllegalArgumentException("Missing token claims")

                    val request = call.receive<CreateAiModelRequest>()

                    val newModel = AiModel(
                        id = UUID.randomUUID(),
                        userId = UUID.fromString(secureUserId),
                        name = request.name,
                        apiKey = request.apiKey,
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now()
                    )

                    val created = aiModelService.addModelConfig(newModel)

                    if (created != null) {
                        call.respond(HttpStatusCode.Created, AiModelResponse(
                            id = created.id.toString(),
                            name = created.name,
                            createdAt = created.createdAt.toString()
                        ))
                    } else {
                        call.respond(HttpStatusCode.InternalServerError, "Failed to save AI configuration")
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid request")
                }
            }

            // 2. Get all models for the logged-in user
            get {
                val principal = call.principal<JWTPrincipal>()
                val secureUserId = principal?.payload?.getClaim("userId")?.asString()

                if (secureUserId == null) {
                    call.respond(HttpStatusCode.Unauthorized, "Missing or invalid token claim")
                    return@get
                }

                try {
                    val userId = UUID.fromString(secureUserId)
                    val models = aiModelService.getUserModels(userId)

                    val response = models.map {
                        AiModelResponse(
                            id = it.id.toString(),
                            name = it.name,
                            createdAt = it.createdAt.toString()
                        )
                    }

                    call.respond(HttpStatusCode.OK, response)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid User ID format")
                }
            }
        }
    }

    // 3. Get all models for a specific user (verifying JWT matches the path)
    route("/api/v1/users/{userId}/models") {
        authenticate("auth-jwt") {
            get {
                val userIdStr = call.parameters["userId"]
                val principal = call.principal<JWTPrincipal>()
                val secureUserId = principal?.payload?.getClaim("userId")?.asString()

                if (userIdStr == null) {
                    call.respond(HttpStatusCode.BadRequest, "Missing user ID")
                    return@get
                }

                if (secureUserId != userIdStr) {
                    call.respond(HttpStatusCode.Forbidden, "Access to other user configurations is denied")
                    return@get
                }

                try {
                    val userId = UUID.fromString(userIdStr)
                    val models = aiModelService.getUserModels(userId)

                    val response = models.map {
                        AiModelResponse(
                            id = it.id.toString(),
                            name = it.name,
                            createdAt = it.createdAt.toString()
                        )
                    }

                    call.respond(HttpStatusCode.OK, response)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid User ID format")
                }
            }
        }
    }
}
    