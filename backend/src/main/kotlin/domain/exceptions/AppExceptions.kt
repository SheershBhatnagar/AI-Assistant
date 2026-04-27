/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 27/04/26 12:12
 */

package dev.sheershbhatnagar.ai_assistant.domain.exceptions

// Base exception for our app
sealed class AppException(message: String) : RuntimeException(message)

class UserAlreadyExistsException(message: String = "User with this email already exists") : AppException(message)
class ResourceNotFoundException(message: String = "The requested resource was not found") : AppException(message)
class ValidationException(message: String = "Invalid input data") : AppException(message)
