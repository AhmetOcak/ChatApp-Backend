package com.model

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: Int = 0,
    val friendshipId: Int,
    val senderEmail: String,
    val receiverEmail: String,
    val messageContent: String,
    val sentAt: String = "",
    val senderImgUrl: String?,
    val senderUsername: String,
    val messageType: MessageType
)

@Serializable
data class PaginatedMessages(
    val messageList: List<Message>,
    val totalPages: Long
)

enum class MessageType {
    TEXT,
    AUDIO,
    IMAGE,
    DOC
}