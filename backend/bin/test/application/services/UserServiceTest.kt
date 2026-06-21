/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 27/04/26 16:19
 */

package dev.sheershbhatnagar.ai_assistant.application.services

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

import dev.sheershbhatnagar.ai_assistant.domain.models.User
import dev.sheershbhatnagar.ai_assistant.domain.repository.UserRepository

class UserServiceTest {

    // 1. Create a "Fake" Repository using MockK
    private val mockRepository = mockk<UserRepository>()

    // 2. Inject the fake repository into our real Service
    private val userService = UserService(mockRepository)

    @Test
    fun `registerUser should successfully save user when email is unique`() = runTest {
        // Arrange: Set up the fake data and rules
        val newUser = User(
            id = UUID.randomUUID(),
            email = "anthony2@stark.com",
            firstName = "Tony",
            middleName = null,
            lastName = "Stark",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        // Rule: When the service checks the DB for the email, return null (meaning it's unique)
        coEvery { mockRepository.getUserByEmail(newUser.email) } returns null
        // Rule: When the service asks to create the user, return the user
        coEvery { mockRepository.createUser(newUser) } returns newUser

        // Act: Actually call the function we are testing
        val result = userService.registerUser(newUser)

        // Assert: Verify the outcome is exactly what we expect
        assertNotNull(result)
        assertEquals("anthony2@stark.com", result?.email)

        // Verify that the repository was actually called exactly once to save the data
        coVerify(exactly = 1) { mockRepository.createUser(newUser) }
    }

    @Test
    fun `registerUser should throw Exception when email already exists`() = runTest {
        // Arrange
        val existingUser = User(
            id = UUID.randomUUID(),
            email = "anthony2@stark.com",
            firstName = "Existing",
            middleName = null,
            lastName = "User",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val newAttempt = existingUser.copy(id = UUID.randomUUID(), firstName = "Imposter")

        // Rule: When the service checks the DB, return the existing user!
        coEvery { mockRepository.getUserByEmail(newAttempt.email) } returns existingUser

        // Act & Assert: Verify that calling the function throws an exception
        val exception = assertThrows(Exception::class.java) {
            runBlocking {
                userService.registerUser(newAttempt)
            }
        }

        assertEquals("A user with this email already exists.", exception.message)

        // Verify that the repository was NEVER called to save the duplicate user
        coVerify(exactly = 0) { mockRepository.createUser(any()) }
    }
}
    