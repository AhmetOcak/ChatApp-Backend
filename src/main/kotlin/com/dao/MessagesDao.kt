package com.dao

import com.model.Message
import org.jetbrains.exposed.sql.ResultRow

interface MessagesDao {
    suspend fun create(
        senderId: Int,
        senderProfilePicUrl: String,
        roomId: Int,
        messageText: String
    ): Message?
    suspend fun getById(roomId: Int, page: Int, pageSize: Int): List<Message>
    suspend fun getTotalItems(roomId: Int, pageSize: Int): Long
    fun rowTo(row: ResultRow): Message
}