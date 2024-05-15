package com.dao

import com.model.UserChatRooms
import org.jetbrains.exposed.sql.ResultRow

interface UserChatRoomsDao {
    suspend fun addUserToRoom(userId: String, roomId: Int): Int?
    suspend fun removeUserFromRoom(userId: String, roomId: Int): Boolean
    suspend fun getUserRooms(userId: String): List<Int>
    fun rowTo(row: ResultRow): UserChatRooms
}