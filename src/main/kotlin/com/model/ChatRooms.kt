package com.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatRooms(
    val id: Int,
    val roomName: String,
    val roomPicUrl: String?,
    val createdAt: String
)