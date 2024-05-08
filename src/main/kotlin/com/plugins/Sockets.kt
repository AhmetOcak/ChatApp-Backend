package com.plugins

import com.model.Message
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json

// Bağlantı sayısı 1 olduğunda son bağlantıda mesaj gönderdiğinde bağlantı otomatik sonlanıyor
fun Application.configureSockets() {
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
    }
    routing {
        val connections = mutableMapOf<String, WebSocketServerSession>()

        webSocket("/chat/{userId}") {
            println("WebSocket connection established.")
            val userId = call.parameters["userId"] ?: return@webSocket
            connections[userId] = this

            try {
                while (coroutineContext.isActive) {
                    // Deserialize message
                    val receivedMessage = receiveDeserialized<Message>()

                    // Broadcast message to specific client
                    connections[receivedMessage.receiverId]?.sendSerialized(receivedMessage)
                }
            } catch (e: Exception) {
                println("Error: ${e.localizedMessage}")
            }
        }
    }
}