/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 24/04/26 17:10
*/

package dev.sheershbhatnagar.ai_assistant.application.services

import java.util.UUID

import dev.sheershbhatnagar.ai_assistant.domain.models.AiModel
import dev.sheershbhatnagar.ai_assistant.domain.repository.AiModelRepository

class AiModelService(private val aiModelRepository: AiModelRepository) {

    suspend fun addModelConfig(aiModel: AiModel): AiModel? {
        if (aiModel.apiKey.isBlank()) {
            throw IllegalArgumentException("API Key cannot be blank.")
        }

        return aiModelRepository.createModelConfig(aiModel)
    }

    suspend fun getUserModels(userId: UUID): List<AiModel> {
        return aiModelRepository.getModelsByUserId(userId)
    }

    suspend fun getModelDetails(modelId: UUID): AiModel? {
        return aiModelRepository.getModelById(modelId)
    }
}
    