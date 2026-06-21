package dev.sheershbhatnagar.ai_assistant.application.services

import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.JWT
import java.util.Date
import java.util.UUID

import dev.sheershbhatnagar.ai_assistant.domain.models.User
import dev.sheershbhatnagar.ai_assistant.domain.repository.UserRepository

class UserService(private val userRepository: UserRepository) {

    suspend fun registerUser(user: User): User? {
        val existingUser = userRepository.getUserByEmail(user.email)

        if (existingUser != null) {
            throw IllegalArgumentException("A user with this email already exists.")
        }

        return userRepository.createUser(user)
    }

    fun generateJwtToken(userId: UUID): String {
        val jwtSecret = System.getenv("JWT_SECRET_TOKEN") ?: "this-is-a-super-secret-jwt-token"

        return JWT.create()
            .withAudience("ai-assistant-users")
            .withIssuer("http://localhost:8080/")
            .withClaim("userId", userId.toString())
            .withExpiresAt(Date(System.currentTimeMillis() + 86400000))
            .sign(Algorithm.HMAC256(jwtSecret))
    }

    suspend fun getUserById(userId: UUID): User? {
        return userRepository.getUserById(userId)
    }
}
