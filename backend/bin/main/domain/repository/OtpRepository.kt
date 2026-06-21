/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 06/05/26 23:43
 */

package dev.sheershbhatnagar.ai_assistant.domain.repository

import java.util.UUID

import dev.sheershbhatnagar.ai_assistant.domain.models.Otp

interface OtpRepository {
    suspend fun createOtp(otp: Otp): Otp?
    suspend fun getValidOtp(userId: UUID, otpCode: String): Otp?
    suspend fun markOtpAsUsed(otpId: UUID)
}
    