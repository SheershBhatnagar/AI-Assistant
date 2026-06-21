/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 27/04/26 12:13
 */

package dev.sheershbhatnagar.ai_assistant.presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String
)
    