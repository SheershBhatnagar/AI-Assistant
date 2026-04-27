package dev.sheershbhatnagar.ai_assistant.presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterUserRequest(
    val email: String,
    val firstName: String,
    val middleName: String? = null,
    val lastName: String? = null
)

@Serializable
data class UserResponse(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String?,
    val token: String
)
