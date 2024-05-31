package com.model

import kotlinx.serialization.Serializable

@Serializable
data class UserChatRooms(
    val userEmail: String,
    val roomId: Int
)
