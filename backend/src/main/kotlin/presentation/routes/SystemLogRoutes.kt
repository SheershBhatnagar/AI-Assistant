/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 24/04/26 18:06
 */

package dev.sheershbhatnagar.ai_assistant.presentation.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDateTime
import java.util.UUID

import dev.sheershbhatnagar.ai_assistant.application.services.SystemLogService
import dev.sheershbhatnagar.ai_assistant.domain.models.SystemLog
import dev.sheershbhatnagar.ai_assistant.presentation.dto.*

fun Route.systemLogRoutes(systemLogService: SystemLogService) {
    route("/api/v1/logs") {

        // 1. Create a new audit log
        post {
            try {
                val request = call.receive<CreateLogRequest>()

                val newLog = SystemLog(
                    id = UUID.randomUUID(),
                    userId = UUID.fromString(request.userId),
                    action = request.action,
                    details = request.details,
                    createdAt = LocalDateTime.now() // It only has a _created_at timestamp
                )

                val created = systemLogService.logAction(newLog)

                if (created != null) {
                    call.respond(HttpStatusCode.Created, SystemLogResponse(
                        id = created.id.toString(),
                        action = created.action,
                        details = created.details,
                        createdAt = created.createdAt.toString()
                    ))
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to create log")
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid request format")
            }
        }
    }

    // 2. Get all logs for a specific user
    route("/api/v1/users/{userId}/logs") {
        get {
            val userIdStr = call.parameters["userId"]

            if (userIdStr == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing user ID")
                return@get
            }

            try {
                val userId = UUID.fromString(userIdStr)
                val logs = systemLogService.getUserLogs(userId)

                val response = logs.map {
                    SystemLogResponse(
                        id = it.id.toString(),
                        action = it.action,
                        details = it.details,
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
