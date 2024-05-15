package com.model

import kotlinx.serialization.Serializable

@Serializable
data class ReceivedMessage(
    val senderId: Int,
    val senderName: String,
    val senderImage: String?,
    val receiverId: Int,
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
    val sentAt: String = ""
)

@Serializable
data class PaginatedMessages(
    val messageList: List<Message>,
    val totalPages: Long
)