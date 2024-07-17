package com.plugins

import com.core.setContent
import com.dao.ChatGroupDao
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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json

fun Application.configureSockets(messagesDao: MessagesDao, fcmTokenDao: FcmTokenDao, chatGroupDao: ChatGroupDao) {
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
                        messageBoxId = receivedMessage.messageBoxId,
                        senderEmail = receivedMessage.senderEmail,
                        messageContent = receivedMessage.messageContent,
                        senderImgUrl = receivedMessage.senderImgUrl,
                        senderUsername = receivedMessage.senderUsername,
                        messageType = receivedMessage.messageType
                    )

                    if (message == null) {
                        call.respond(HttpStatusCode.InternalServerError, message = "Message could not be send.")
                    } else {

                        val participants = chatGroupDao.getGroupParticipants(receivedMessage.messageBoxId)
                        participants.forEach { email ->
                            // Send messages to specific client/clients only when client online
                            try {
                                connections[email]?.sendSerialized(message)
                            } catch (e: CancellationException) {
                                // If target client lost its websocket connection we remove its email
                                connections.remove(email)
                            }

                            if (email != message.senderEmail) {
                                // If target client isn't online we will send notification message thought firebase
                                if (connections[email] == null) {
                                    // Send notification message to every participant
                                    fcmTokenDao.get(email)?.let {
                                        sendFcmMessage(
                                            notificationContent = NotificationContent(
                                                title = message.senderUsername,
                                                message = setContent(message)
                                            ),
                                            token = it
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, e.stackTraceToString())
            }
        }
    }
}