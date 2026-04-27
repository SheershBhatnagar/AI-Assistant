/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 24/04/26 17:54
 */

package dev.sheershbhatnagar.ai_assistant.domain.repository

import java.util.UUID

import dev.sheershbhatnagar.ai_assistant.domain.models.SystemLog

interface SystemLogRepository {
    suspend fun createLog(log: SystemLog): SystemLog?
    suspend fun getLogsByUserId(userId: UUID): List<SystemLog>
}
    