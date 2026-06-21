/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 24/04/26 16:24
 */

package dev.sheershbhatnagar.ai_assistant.presentation.routes

import io.ktor.http.*
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
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

        authenticate("auth-jwt") {

            post("/conversations") {

                val principal = call.principal<JWTPrincipal>()
                val secureUserId = principal?.payload?.getClaim("userId")?.asString()

                    val request = call.receive<CreateConversationRequest>()

                    val newConversation = Conversation(
                        id = UUID.randomUUID(),
                        userId = UUID.fromString(secureUserId),
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
                    }
            }

            post("/messages") {

                val principal = call.principal<JWTPrincipal>()
                val secureUserId = principal?.payload?.getClaim("userId")?.asString()

                    val request = call.receive<SendMessageRequest>()

                    val newMessage = Message(
                        id = UUID.randomUUID(),
                        userId = UUID.fromString(secureUserId),
                        conversationId = UUID.fromString(request.conversationId),
                        modelId = request.modelId?.let { UUID.fromString(it) },
                        content = request.content,
                        senderType = request.senderType,
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now()
                    )

                    val created = chatService.processUserMessage(newMessage)

                    if (created != null) {
                        call.respond(HttpStatusCode.Created, MessageResponse(
                            id = created.id.toString(),
                            senderType = created.senderType,
                            content = created.content,
                            createdAt = created.createdAt.toString()
                        ))
                    }
            }

            get("/conversations") {
                val principal = call.principal<JWTPrincipal>()
                val secureUserId = principal?.payload?.getClaim("userId")?.asString()

                if (secureUserId == null) {
                    call.respond(HttpStatusCode.Unauthorized, "Missing or invalid token claim")
                    return@get
                }

                val userId = UUID.fromString(secureUserId)
                val conversations = chatService.getUserConversations(userId)

                val response = conversations.map {
                    ConversationResponse(
                        id = it.id.toString(),
                        title = it.title,
                        createdAt = it.createdAt.toString()
                    )
                }
                call.respond(HttpStatusCode.OK, response)
            }

            get("/conversations/{id}/messages") {
                val conversationIdStr = call.parameters["id"] ?: throw IllegalArgumentException("Missing conversation ID")
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
            }
        }
    }
}
