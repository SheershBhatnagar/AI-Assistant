/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 24/04/26 17:16
 */

package dev.sheershbhatnagar.ai_assistant.presentation.routes

import io.ktor.http.*
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

        // 1. Add a new AI Model configuration
        post {
            try {
                val request = call.receive<CreateAiModelRequest>()

                val newModel = AiModel(
                    id = UUID.randomUUID(),
                    userId = UUID.fromString(request.userId),
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
    }

    // 2. Get all models for a specific user
    route("/api/v1/users/{userId}/models") {
        get {
            val userIdStr = call.parameters["userId"]

            if (userIdStr == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing user ID")
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
    