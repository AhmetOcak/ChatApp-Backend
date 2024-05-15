package com.model

import kotlinx.serialization.Serializable

@Serializable
data class SendMessage(
    val senderId: String,
    val senderName: String,
    val senderImage: String?,
    val receiverId: String,
    val content: String,
    val time: String
)

@Serializable
data class ReceiveMessage(
    val senderId: String,
    val senderName: String,
    val senderImage: String?,
    val content: String,
    val time: String
)

fun SendMessage.toReceiveMessage(): ReceiveMessage {
    return ReceiveMessage(
        senderId = senderId,
        senderName = senderName,
        senderImage = senderImage,
        content = content,
        time = time
    )
}

@Serializable
data class Message(
    val id: Int,
    val senderId: String,
    val senderProfilePicUrl: String?,
    val roomId: Int,
    val messageText: String,
    val sentAt: String
)

@Serializable
data class PaginatedMessages(
    val messageList: List<Message>,
    val totalPages: Long
)