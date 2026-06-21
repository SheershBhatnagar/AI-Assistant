/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 27/05/26 21:04
 */

package dev.sheershbhatnagar.ai_assistant.domain.repository

import java.util.UUID
import dev.sheershbhatnagar.ai_assistant.domain.models.UserSettings

interface UserSettingsRepository {
    suspend fun getSettingsByUserId(userId: UUID): UserSettings?
    suspend fun upsertSettings(settings: UserSettings): UserSettings?
}
