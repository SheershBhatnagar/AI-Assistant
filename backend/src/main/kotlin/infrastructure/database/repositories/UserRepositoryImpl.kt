package dev.sheershbhatnagar.ai_assistant.infrastructure.database.repositories

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

import dev.sheershbhatnagar.ai_assistant.domain.models.User
import dev.sheershbhatnagar.ai_assistant.domain.repository.UserRepository
import dev.sheershbhatnagar.ai_assistant.infrastructure.database.entities.UsersTable

class UserRepositoryImpl : UserRepository {

    private fun resultRowToUser(row: ResultRow) = User(
        id = row[UsersTable.id],
        email = row[UsersTable.email],
        firstName = row[UsersTable.firstName],
        middleName = row[UsersTable.middleName],
        lastName = row[UsersTable.lastName],
        createdAt = row[UsersTable.createdAt],
        updatedAt = row[UsersTable.updatedAt]
    )

    override suspend fun createUser(user: User): User? {
        return newSuspendedTransaction {
            val insertStatement = UsersTable.insert {
                it[id] = user.id
                it[email] = user.email
                it[firstName] = user.firstName
                it[middleName] = user.middleName
                it[lastName] = user.lastName
                it[createdAt] = user.createdAt
                it[updatedAt] = user.updatedAt
            }

            insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToUser)
        }
    }

    override suspend fun getUserByEmail(email: String): User? {
        return newSuspendedTransaction {
            UsersTable.select { UsersTable.email eq email }
                .map(::resultRowToUser)
                .singleOrNull()
        }
    }
}
