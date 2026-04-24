package dev.sheershbhatnagar.ai_assistant.presentation.routes

/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 24/04/26 16:24
 */

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDateTime
import java.util.UUID

import dev.sheershbhatnagar.ai_assistant.application.services.ChatService
import dev.sheershbhatnagar.ai_assistant.domain.models.Conversation
import dev.sheershbhatnagar.ai_assistant.domain.models.Message
import dev.sheershbhatnagar.ai_assistant.presentation.dto.*

fun Route.chatRoutes(chatService: ChatService) {
    route("/api/v1/chat") {

        post("/conversations") {
            try {
                val request = call.receive<CreateConversationRequest>()

                val newConversation = Conversation(
                    id = UUID.randomUUID(),
                    userId = UUID.fromString(request.userId),
                    title = request.title,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )

                val created = chatService.startConversation(newConversation)

                if (created != null) {
                    call.respond(HttpStatusCode.Created, ConversationResponse(
                        id = created.id.toString(),
                        title = created.title,
                        createdAt = created.createdAt.toString()
                    ))
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to create conversation")
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid request")
            }
        }

        post("/messages") {
            try {
                val request = call.receive<SendMessageRequest>()

                val newMessage = Message(
                    id = UUID.randomUUID(),
                    userId = UUID.fromString(request.userId),
                    conversationId = UUID.fromString(request.conversationId),
                    modelId = UUID.fromString(request.modelId),
                    content = request.content,
                    senderType = request.senderType,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )

                val created = chatService.addMessage(newMessage)

                if (created != null) {
                    call.respond(HttpStatusCode.Created, MessageResponse(
                        id = created.id.toString(),
                        senderType = created.senderType,
                        content = created.content,
                        createdAt = created.createdAt.toString()
                    ))
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to send message")
                }
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Bad Request")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid request format")
            }
        }

        get("/conversations/{id}/messages") {
            val conversationIdStr = call.parameters["id"]

            if (conversationIdStr == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing conversation ID")
                return@get
            }

            try {
                val conversationId = UUID.fromString(conversationIdStr)
                val messages = chatService.getConversationHistory(conversationId)

                val response = messages.map {
                    MessageResponse(
                        id = it.id.toString(),
                        senderType = it.senderType,
                        content = it.content,
                        createdAt = it.createdAt.toString()
                    )
                }

                call.respond(HttpStatusCode.OK, response)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
            }
        }
    }
}
