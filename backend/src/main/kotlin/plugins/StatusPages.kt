/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 27/04/26 12:17
 */

package dev.sheershbhatnagar.ai_assistant.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

import dev.sheershbhatnagar.ai_assistant.domain.exceptions.*
import dev.sheershbhatnagar.ai_assistant.presentation.dto.ErrorResponse

fun Application.configureStatusPages() {
    install(StatusPages) {

        // Catch our custom exceptions
        exception<UserAlreadyExistsException> { call, cause ->
            call.respond(
                HttpStatusCode.Conflict,
                ErrorResponse(HttpStatusCode.Conflict.value, "Conflict", cause.message ?: "User exists")
            )
        }

        exception<ResourceNotFoundException> { call, cause ->
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse(HttpStatusCode.NotFound.value, "Not Found", cause.message ?: "Not found")
            )
        }

        exception<ValidationException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(HttpStatusCode.BadRequest.value, "Bad Request", cause.message ?: "Invalid input")
            )
        }

        // Catch standard Kotlin validation errors
        exception<IllegalArgumentException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(HttpStatusCode.BadRequest.value, "Bad Request", cause.message ?: "Invalid argument")
            )
        }

        // The Ultimate Safety Net: Catch ANY unhandled exception (500 Internal Server Error)
        exception<Throwable> { call, cause ->
            call.application.environment.log.error("Unhandled Exception caught", cause)
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(HttpStatusCode.InternalServerError.value, "Internal Server Error", "Something went wrong on our end")
            )
        }
    }
}
