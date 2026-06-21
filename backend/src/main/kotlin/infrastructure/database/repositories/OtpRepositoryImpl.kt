/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 06/05/26 23:52
 */

package dev.sheershbhatnagar.ai_assistant.infrastructure.database.repositories

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDateTime
import java.util.UUID

import dev.sheershbhatnagar.ai_assistant.domain.models.Otp
import dev.sheershbhatnagar.ai_assistant.domain.repository.OtpRepository
import dev.sheershbhatnagar.ai_assistant.infrastructure.database.entities.OtpsTable

class OtpRepositoryImpl : OtpRepository {

    // --- Mapper ---
    private fun rowToOtp(row: ResultRow) = Otp(
        id = row[OtpsTable.id],
        userId = row[OtpsTable.userId],
        otp = row[OtpsTable.otp],
        expiresAt = row[OtpsTable.expiresAt],
        isUsed = row[OtpsTable.isUsed],
        createdAt = row[OtpsTable.createdAt],
        updatedAt = row[OtpsTable.updatedAt]
    )

    override suspend fun createOtp(otp: Otp): Otp? {
        return newSuspendedTransaction {
            val statement = OtpsTable.insert {
                it[id] = otp.id
                it[userId] = otp.userId
                it[this.otp] = otp.otp
                it[expiresAt] = otp.expiresAt
                it[isUsed] = otp.isUsed
                it[createdAt] = otp.createdAt
                it[updatedAt] = otp.updatedAt
            }
            statement.resultedValues?.singleOrNull()?.let(::rowToOtp)
        }
    }

    override suspend fun getValidOtp(userId: UUID, otpCode: String): Otp? {
        return newSuspendedTransaction {
            OtpsTable.select {
                (OtpsTable.userId eq userId) and (OtpsTable.otp eq otpCode)
            }
                .orderBy(OtpsTable.createdAt to SortOrder.DESC) // Fetches the most recently generated OTP
                .limit(1)
                .singleOrNull()?.let(::rowToOtp)
        }
    }

    override suspend fun markOtpAsUsed(otpId: UUID) {
        newSuspendedTransaction {
            OtpsTable.update({ OtpsTable.id eq otpId }) {
                it[isUsed] = true
                it[updatedAt] = LocalDateTime.now() // Always keep your audit timestamps accurate
            }
        }
    }
}
