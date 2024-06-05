package com.model

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: Int = 0,
    val senderEmail: String,
    val receiverEmail: String,
    val messageText: String,
    val sentAt: String = "",
    val senderImgUrl: String?,
    val senderUsername: String
)

@Serializable
data class PaginatedMessages(
    val messageList: List<Message>,
    val totalPages: Long
)