package com.plugins

import com.core.toReceiveMessage
import com.dao.MessagesDao
import com.model.SendMessage
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json

// TODO: Bir client tarafından mesaj içeriği aynı olan mesajlar arka arkaya gönderilemiyor.
// TODO: Bağlantı sayısı 1 olduğunda son bağlantıda mesaj gönderdiğinde bağlantı otomatik sonlanıyor.
fun Application.configureSockets(messagesDao: MessagesDao) {
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
    }
    routing {
        val connections = mutableMapOf<String, WebSocketServerSession>()

        webSocket("/chat/{userId}") {
            val userId = call.parameters["userId"] ?: return@webSocket
            connections[userId] = this

            try {
                while (coroutineContext.isActive) {
                    // Deserialize message
                    val receivedMessage = receiveDeserialized<SendMessage>()

                    // Broadcast message to specific client
                    connections[receivedMessage.receiverId]?.sendSerialized(receivedMessage.toReceiveMessage())
                }
            } catch (e: Exception) {
                println("Error: ${e.localizedMessage}")
            }
        }
    }
}