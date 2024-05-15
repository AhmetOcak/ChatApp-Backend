package com.model

import kotlinx.serialization.Serializable

@Serializable
data class SendMessage(
    val senderId: Int,
    val senderName: String,
    val senderImage: String?,
    val receiverId: String,
    val content: String,
    val time: String
)

@Serializable
data class ReceiveMessage(
    val senderId: Int,
    val senderName: String,
    val senderImage: String?,
    val content: String,
    val time: String
)

@Serializable
data class Message(
    val id: Int = 0,
    val senderId: Int,
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