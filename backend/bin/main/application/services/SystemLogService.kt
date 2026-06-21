/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 24/04/26 17:56
 */

package dev.sheershbhatnagar.ai_assistant.application.services

import java.util.UUID

import dev.sheershbhatnagar.ai_assistant.domain.models.SystemLog
import dev.sheershbhatnagar.ai_assistant.domain.repository.SystemLogRepository

class SystemLogService(private val systemLogRepository: SystemLogRepository) {

    suspend fun logAction(log: SystemLog): SystemLog? {
        return systemLogRepository.createLog(log)
    }

    suspend fun getUserLogs(userId: UUID): List<SystemLog> {
        return systemLogRepository.getLogsByUserId(userId)
    }
}
    