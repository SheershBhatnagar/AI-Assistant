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

@Serializable
data class SendOtpRequest(val email: String)

@Serializable
data class VerifyOtpRequest(val email: String, val otp: String)

@Serializable
data class AuthResponse(val token: String, val message: String)

@Serializable
data class UserProfileResponse(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String?
)

