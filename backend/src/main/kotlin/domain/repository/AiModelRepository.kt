/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 24/04/26 17:03
 */

package dev.sheershbhatnagar.ai_assistant.domain.repository

import java.util.UUID

import dev.sheershbhatnagar.ai_assistant.domain.models.AiModel

interface AiModelRepository {
    suspend fun createModelConfig(aiModel: AiModel): AiModel?
    suspend fun getModelsByUserId(userId: UUID): List<AiModel>
    suspend fun getModelById(modelId: UUID): AiModel?
}
