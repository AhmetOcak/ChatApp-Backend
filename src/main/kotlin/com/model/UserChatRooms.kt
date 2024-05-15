package com.model

import kotlinx.serialization.Serializable

@Serializable
data class UserChatRooms(
    val userId: Int,
    val roomId: Int
)
