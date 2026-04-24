package dev.sheershbhatnagar.ai_assistant.application.services

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
}
