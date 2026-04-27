package dev.sheershbhatnagar.ai_assistant.presentation.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDateTime
import java.util.UUID

import dev.sheershbhatnagar.ai_assistant.application.services.UserService
import dev.sheershbhatnagar.ai_assistant.domain.models.User
import dev.sheershbhatnagar.ai_assistant.presentation.dto.RegisterUserRequest
import dev.sheershbhatnagar.ai_assistant.presentation.dto.UserResponse

fun Route.userRoutes(userService: UserService) {

    route("/api/v1/users") {

        post("/register") {

                val request = call.receive<RegisterUserRequest>()

                val newUser = User(
                    id = UUID.randomUUID(),
                    email = request.email,
                    firstName = request.firstName,
                    middleName = request.middleName,
                    lastName = request.lastName,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )

                val createdUser = userService.registerUser(newUser)

                if (createdUser != null) {
                    val response = UserResponse(
                        id = createdUser.id.toString(),
                        email = createdUser.email,
                        firstName = createdUser.firstName,
                        lastName = createdUser.lastName,
                        token = userService.generateJwtToken(createdUser.id)
                    )

                    call.respond(HttpStatusCode.Created, response)
                }
        }
    }
}
