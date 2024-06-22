package com.plugins

import com.dao.FcmTokenDao
import com.dao.MessagesDao
import com.firebase.sendFcmMessage
import com.model.Message
import com.model.NotificationContent
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json

fun Application.configureSockets(messagesDao: MessagesDao, fcmTokenDao: FcmTokenDao) {
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
    }
    routing {
        val connections = mutableMapOf<String, WebSocketServerSession>()

        webSocket("/chat/{userEmail}") {
            val userEmail = call.parameters["userEmail"] ?: return@webSocket
            connections[userEmail] = this

            try {
                while (coroutineContext.isActive) {
                    // Deserialize message
                    val receivedMessage = receiveDeserialized<Message>()

                    val message = messagesDao.create(
                        friendshipId = receivedMessage.friendshipId,
                        senderEmail = receivedMessage.senderEmail,
                        receiverEmail = receivedMessage.receiverEmail,
                        messageContent = receivedMessage.messageContent,
                        senderImgUrl = receivedMessage.senderImgUrl,
                        senderUsername = receivedMessage.senderUsername,
                        messageType = receivedMessage.messageType
                    )

                    if (message == null) {
                        call.respond(HttpStatusCode.InternalServerError, message = "Message could not be send.")
                    } else {
                        // Broadcast message to specific client
                        connections[message.senderEmail]?.sendSerialized(message)
                        connections[message.receiverEmail]?.sendSerialized(message)

                        fcmTokenDao.get(message.receiverEmail)?.let {
                            sendFcmMessage(
                                notificationContent = NotificationContent(
                                    title = message.senderUsername,
                                    message = message.messageContent
                                ),
                                token = it
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, e.stackTraceToString())
            }
        }
    }
}