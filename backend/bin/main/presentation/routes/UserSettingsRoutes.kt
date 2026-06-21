/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 27/05/26 21:10
 */

package dev.sheershbhatnagar.ai_assistant.presentation.routes

import io.ktor.http.*
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

import dev.sheershbhatnagar.ai_assistant.application.services.UserSettingsService
import dev.sheershbhatnagar.ai_assistant.presentation.dto.UpdateUserSettingsRequest
import dev.sheershbhatnagar.ai_assistant.presentation.dto.UserSettingsResponse

fun Route.userSettingsRoutes(userSettingsService: UserSettingsService) {
    route("/api/v1/users/settings") {
        authenticate("auth-jwt") {

            // 1. Get logged-in user's settings
            get {
                val principal = call.principal<JWTPrincipal>()
                val secureUserId = principal?.payload?.getClaim("userId")?.asString()

                if (secureUserId == null) {
                    call.respond(HttpStatusCode.Unauthorized, "Missing or invalid token claim")
                    return@get
                }

                try {
                    val userId = UUID.fromString(secureUserId)
                    val settings = userSettingsService.getUserSettings(userId)

                    if (settings != null) {
                        call.respond(HttpStatusCode.OK, UserSettingsResponse(
                            id = settings.id.toString(),
                            userId = settings.userId.toString(),
                            defaultModelId = settings.defaultModelId.toString(),
                            createdAt = settings.createdAt.toString(),
                            updatedAt = settings.updatedAt.toString()
                        ))
                    } else {
                        call.respond(HttpStatusCode.NotFound, "No settings configured for user")
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid User ID format")
                }
            }

            // 2. Create/Update logged-in user's default model setting
            put {
                val principal = call.principal<JWTPrincipal>()
                val secureUserId = principal?.payload?.getClaim("userId")?.asString()

                if (secureUserId == null) {
                    call.respond(HttpStatusCode.Unauthorized, "Missing or invalid token claim")
                    return@put
                }

                try {
                    val request = call.receive<UpdateUserSettingsRequest>()
                    val userId = UUID.fromString(secureUserId)
                    val defaultModelId = UUID.fromString(request.defaultModelId)

                    val updated = userSettingsService.updateDefaultModel(userId, defaultModelId)

                    call.respond(HttpStatusCode.OK, UserSettingsResponse(
                        id = updated.id.toString(),
                        userId = updated.userId.toString(),
                        defaultModelId = updated.defaultModelId.toString(),
                        createdAt = updated.createdAt.toString(),
                        updatedAt = updated.updatedAt.toString()
                    ))
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid request parameters")
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to update settings: ${e.message}")
                }
            }
        }
    }
}
