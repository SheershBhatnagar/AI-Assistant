/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 24/04/26 18:19
 */

package dev.sheershbhatnagar.ai_assistant.infrastructure.external

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.*

class AiClient(private val client: HttpClient) {

    suspend fun callGemini(modelName: String, apiKey: String, prompt: String): String {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent"

        val response = client.post(url) {
            header("x-goog-api-key", apiKey)
            contentType(ContentType.Application.Json)
            setBody(buildJsonObject {
                putJsonArray("contents") {
                    addJsonObject {
                        putJsonArray("parts") {
                            addJsonObject { put("text", prompt) }
                        }
                    }
                }
            })
        }

        // Basic parsing of Gemini's specific JSON structure
        val jsonResponse = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        return jsonResponse["candidates"]?.jsonArray?.get(0)?.jsonObject?.get("content")
            ?.jsonObject?.get("parts")?.jsonArray?.get(0)?.jsonObject?.get("text")
            ?.jsonPrimitive?.content ?: "No response from Gemini"
    }

    suspend fun callOpenAI(modelName: String, apiKey: String, prompt: String): String {
        val response = client.post("https://api.openai.com/v1/chat/completions") {
            header(HttpHeaders.Authorization, "Bearer $apiKey")
            contentType(ContentType.Application.Json)
            setBody(buildJsonObject {
                put("model", modelName)
                putJsonArray("messages") {
                    addJsonObject {
                        put("role", "user")
                        put("content", prompt)
                    }
                }
            })
        }

        val jsonResponse = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        return jsonResponse["choices"]?.jsonArray?.get(0)?.jsonObject?.get("message")
            ?.jsonObject?.get("content")?.jsonPrimitive?.content ?: "No response from OpenAI"
    }
}
