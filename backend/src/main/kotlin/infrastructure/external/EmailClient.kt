/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 07/05/26 01:04
 */

package dev.sheershbhatnagar.ai_assistant.infrastructure.external

interface EmailClient {
    suspend fun sendEmail(toAddress: String, subject: String, htmlBody: String): Boolean
}
