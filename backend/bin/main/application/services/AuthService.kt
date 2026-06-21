/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 06/05/26 23:44
 */

package dev.sheershbhatnagar.ai_assistant.application.services

import java.time.LocalDateTime
import java.util.UUID
import kotlin.random.Random

import dev.sheershbhatnagar.ai_assistant.domain.models.Otp
import dev.sheershbhatnagar.ai_assistant.domain.models.User
import dev.sheershbhatnagar.ai_assistant.domain.repository.OtpRepository
import dev.sheershbhatnagar.ai_assistant.domain.repository.UserRepository
import dev.sheershbhatnagar.ai_assistant.infrastructure.external.EmailClient

class AuthService(
    private val userRepository: UserRepository,
    private val otpRepository: OtpRepository,
    private val userService: UserService,
    private val emailClient: EmailClient,
) {
    // STEP 1: Send the OTP
    suspend fun sendOtp(email: String): Boolean {
        // 1. Find the user, or create a new one if they don't exist
        var user = userRepository.getUserByEmail(email)

        if (user == null) {
            user = User(
                id = UUID.randomUUID(),
                email = email,
                firstName = "User", // Can prompt for actual name later
                middleName = null,
                lastName = null,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )

            userRepository.createUser(user)
        }

        // 2. Generate a 6-digit OTP
        val otpCode = Random.nextInt(100000, 999999).toString()

        // 3. Save OTP to database (Expires in 10 minutes)
        val newOtp = Otp(
            id = UUID.randomUUID(),
            userId = user.id,
            otp = otpCode,
            expiresAt = LocalDateTime.now().plusMinutes(10),
            isUsed = false,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        otpRepository.createOtp(newOtp)

        // 4. "Send" the email
        val emailBody = """
            <div style="font-family: Arial, sans-serif; text-align: center; padding: 20px;">
                <h2>Welcome to AI Assistant</h2>
                <p>Your secure One-Time Password (OTP) to log in is:</p>
                <h1 style="color: #4A90E2; letter-spacing: 5px;">$otpCode</h1>
                <p style="color: #888;">This code will expire in 10 minutes. Do not share it with anyone.</p>
            </div>
        """.trimIndent()

        // 3. SEND IT!
        val isSent = emailClient.sendEmail(
            toAddress = email,
            subject = "Your Login Code: $otpCode",
            htmlBody = emailBody
        )

        if (!isSent) {
            throw RuntimeException("Failed to send the OTP email. Please try again later.")
        }

        return true
    }

    // STEP 2: Verify the OTP and issue JWT
    suspend fun verifyOtpAndLogin(email: String, otpCode: String): String {
        val user = userRepository.getUserByEmail(email)
            ?: throw IllegalArgumentException("User not found")

        // 1. Fetch valid OTP from database
        val otpRecord = otpRepository.getValidOtp(user.id, otpCode)
            ?: throw IllegalArgumentException("Invalid OTP")

        // 2. Validate expiration and usage
        if (otpRecord.isUsed) throw IllegalArgumentException("OTP has already been used")
        if (otpRecord.expiresAt.isBefore(LocalDateTime.now())) throw IllegalArgumentException("OTP has expired")

        // 3. Mark as used so it cannot be reused
        otpRepository.markOtpAsUsed(otpRecord.id)

        // 4. Issue the JWT for session management
        return userService.generateJwtToken(user.id)
    }
}
    