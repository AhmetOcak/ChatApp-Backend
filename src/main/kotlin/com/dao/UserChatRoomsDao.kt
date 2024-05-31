package com.dao

import com.model.UserChatRooms
import org.jetbrains.exposed.sql.ResultRow

interface UserChatRoomsDao {
    suspend fun addUserToRoom(userEmail: String, roomId: Int): Int?
    suspend fun removeUserFromRoom(userEmail: String, roomId: Int): Boolean
    suspend fun getUserRooms(userEmail: String): List<Int>
    fun rowTo(row: ResultRow): UserChatRooms
}