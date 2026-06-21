/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 27/04/26 12:36
 */

package dev.sheershbhatnagar.ai_assistant.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

fun Application.configureSecurity() {
    // In a real app, NEVER hardcode your secret. Load this from application.yaml!
    val jwtSecret = System.getenv("JWT_SECRET_TOKEN") ?: "this-is-a-super-secret-jwt-token"
    val jwtIssuer = "http://localhost:8080/"
    val jwtAudience = "ai-assistant-users"
    val jwtRealm = "AI Assistant App"

    install(Authentication) {
        jwt("auth-jwt") {
            realm = jwtRealm
            verifier(
                JWT.require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtIssuer)
                    .build()
            )
            validate { credential ->
                // If the token is valid and has a payload, accept it
                if (credential.payload.audience.contains(jwtAudience)) {
                    JWTPrincipal(credential.payload)
                } else null
            }
            challenge { defaultScheme, realm ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }
}
