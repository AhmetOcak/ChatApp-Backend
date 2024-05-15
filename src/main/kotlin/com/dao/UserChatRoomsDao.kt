package com.dao

import com.model.UserChatRooms
import org.jetbrains.exposed.sql.ResultRow

interface UserChatRoomsDao {
    suspend fun addUserToRoom(userId: Int, roomId: Int): Int?
    suspend fun removeUserFromRoom(userId: Int, roomId: Int): Boolean
    suspend fun getUserRooms(userId: Int): List<Int>
    fun rowTo(row: ResultRow): UserChatRooms
}