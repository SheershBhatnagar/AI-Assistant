package dev.sheershbhatnagar.ai_assistant.domain.repository

import java.util.UUID

import dev.sheershbhatnagar.ai_assistant.domain.models.User

interface UserRepository {
    suspend fun createUser(user: User): User?
    suspend fun getUserByEmail(email: String): User?
}
