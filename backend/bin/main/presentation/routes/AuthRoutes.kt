/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 06/05/26 23:47
 */

package dev.sheershbhatnagar.ai_assistant.presentation.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

import dev.sheershbhatnagar.ai_assistant.application.services.AuthService
import dev.sheershbhatnagar.ai_assistant.presentation.dto.AuthResponse
import dev.sheershbhatnagar.ai_assistant.presentation.dto.SendOtpRequest
import dev.sheershbhatnagar.ai_assistant.presentation.dto.VerifyOtpRequest

fun Route.authRoutes(authService: AuthService) {
    route("/api/v1/auth") {

        post("/send-otp") {
            val request = call.receive<SendOtpRequest>()
            authService.sendOtp(request.email)
            call.respond(HttpStatusCode.OK, mapOf("message" to "OTP sent successfully to ${request.email}"))
        }

        post("/verify-otp") {
            val request = call.receive<VerifyOtpRequest>()
            // If verification succeeds, this returns our JWT
            val jwtToken = authService.verifyOtpAndLogin(request.email, request.otp)

            call.respond(HttpStatusCode.OK, AuthResponse(
                token = jwtToken,
                message = "Authentication successful"
            ))
        }
    }
}
    