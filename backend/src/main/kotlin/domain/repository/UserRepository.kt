package dev.sheershbhatnagar.ai_assistant.domain.repository

import dev.sheershbhatnagar.ai_assistant.domain.models.User

import java.util.UUID

interface UserRepository {
    suspend fun createUser(user: User): User?
    suspend fun getUserByEmail(email: String): User?
    suspend fun getUserById(id: UUID): User?
}
