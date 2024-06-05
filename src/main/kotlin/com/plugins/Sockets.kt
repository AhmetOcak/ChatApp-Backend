package com.plugins

import com.dao.MessagesDao
import com.model.Message
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json

// TODO: Bağlantı sayısı 1 olduğunda son bağlantıda mesaj gönderdiğinde bağlantı otomatik sonlanıyor.
fun Application.configureSockets(messagesDao: MessagesDao) {
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
                        senderEmail = receivedMessage.senderEmail,
                        receiverEmail = receivedMessage.receiverEmail,
                        messageText = receivedMessage.messageText,
                        senderImgUrl = receivedMessage.senderImgUrl,
                        senderUsername = receivedMessage.senderUsername
                    )

                    if (message == null) {
                        call.respond(HttpStatusCode.InternalServerError, message = "Message could not be send.")
                    } else {
                        // Broadcast message to specific client
                        connections[message.senderEmail]?.sendSerialized(message)
                        connections[message.receiverEmail]?.sendSerialized(message)
                    }
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, e.stackTraceToString())
            }
        }
    }
}