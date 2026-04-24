package dev.sheershbhatnagar.ai_assistant.infrastructure.database.repositories

/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 24/04/26 17:05
 */

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID

import dev.sheershbhatnagar.ai_assistant.domain.models.AiModel
import dev.sheershbhatnagar.ai_assistant.domain.repository.AiModelRepository
import dev.sheershbhatnagar.ai_assistant.infrastructure.database.entities.ModelsTable

class AiModelRepositoryImpl : AiModelRepository {

    private fun rowToAiModel(row: ResultRow) = AiModel(
        id = row[ModelsTable.id],
        userId = row[ModelsTable.userId],
        name = row[ModelsTable.name],
        apiKey = row[ModelsTable.apiKey],
        createdAt = row[ModelsTable.createdAt],
        updatedAt = row[ModelsTable.updatedAt]
    )

    override suspend fun createModelConfig(aiModel: AiModel): AiModel? {
        return newSuspendedTransaction {
            val statement = ModelsTable.insert {
                it[id] = aiModel.id
                it[userId] = aiModel.userId
                it[name] = aiModel.name
                it[apiKey] = aiModel.apiKey
                it[createdAt] = aiModel.createdAt
                it[updatedAt] = aiModel.updatedAt
            }
            statement.resultedValues?.singleOrNull()?.let(::rowToAiModel)
        }
    }

    override suspend fun getModelsByUserId(userId: UUID): List<AiModel> {
        return newSuspendedTransaction {
            ModelsTable.select { ModelsTable.userId eq userId }
                .map(::rowToAiModel)
        }
    }

    override suspend fun getModelById(modelId: UUID): AiModel? {
        return newSuspendedTransaction {
            ModelsTable.select { ModelsTable.id eq modelId }
                .singleOrNull()?.let(::rowToAiModel)
        }
    }
}
    