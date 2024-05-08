package com.model

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val senderName: String,
    val receiverId: String,
    val content: String
)