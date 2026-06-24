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

import dev.sheershbhatnagar.ai_assistant.application.services.UserService
import dev.sheershbhatnagar.ai_assistant.domain.models.User
import dev.sheershbhatnagar.ai_assistant.presentation.dto.RegisterUserRequest
import dev.sheershbhatnagar.ai_assistant.presentation.dto.UserResponse
import dev.sheershbhatnagar.ai_assistant.presentation.dto.UserProfileResponse
import dev.sheershbhatnagar.ai_assistant.presentation.dto.UpdateProfileRequest

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

        authenticate("auth-jwt") {
            get("/profile") {
                val principal = call.principal<JWTPrincipal>()
                val secureUserId = principal?.payload?.getClaim("userId")?.asString()

                if (secureUserId == null) {
                    call.respond(HttpStatusCode.Unauthorized, "Missing or invalid token claim")
                    return@get
                }

                try {
                    val userId = UUID.fromString(secureUserId)
                    val user = userService.getUserById(userId)

                    if (user != null) {
                        call.respond(HttpStatusCode.OK, UserProfileResponse(
                            id = user.id.toString(),
                            email = user.email,
                            firstName = user.firstName,
                            lastName = user.lastName
                        ))
                    } else {
                        call.respond(HttpStatusCode.NotFound, "User not found")
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid User ID format")
                }
            }

            put("/profile") {
                val principal = call.principal<JWTPrincipal>()
                val secureUserId = principal?.payload?.getClaim("userId")?.asString()

                if (secureUserId == null) {
                    call.respond(HttpStatusCode.Unauthorized, "Missing or invalid token claim")
                    return@put
                }

                try {
                    val userId = UUID.fromString(secureUserId)
                    val request = call.receive<UpdateProfileRequest>()
                    
                    if (request.firstName.isBlank()) {
                        call.respond(HttpStatusCode.BadRequest, "First name cannot be empty")
                        return@put
                    }

                    val updated = userService.updateUser(userId, request.firstName, request.lastName)

                    if (updated != null) {
                        call.respond(HttpStatusCode.OK, UserProfileResponse(
                            id = updated.id.toString(),
                            email = updated.email,
                            firstName = updated.firstName,
                            lastName = updated.lastName
                        ))
                    } else {
                        call.respond(HttpStatusCode.NotFound, "User not found")
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid request or user ID")
                }
            }
        }
    }
}
