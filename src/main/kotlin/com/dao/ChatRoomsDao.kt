package com.dao

import com.model.ChatRooms
import org.jetbrains.exposed.sql.ResultRow

interface ChatRoomsDao {
    suspend fun create(
        roomName: String,
        roomPicUrl: String?
    ): ChatRooms?

    suspend fun getById(id: Int): ChatRooms?
    suspend fun updateRoom(roomId: Int, roomName: String?, roomProfilePic: String?): Boolean

    fun rowTo(row: ResultRow): ChatRooms
}