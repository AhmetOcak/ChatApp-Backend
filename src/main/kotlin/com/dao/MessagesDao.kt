package com.dao

import com.model.Message
import com.model.MessageType
import org.jetbrains.exposed.sql.ResultRow

interface MessagesDao {
    suspend fun create(
        messageBoxId: Int,
        senderEmail: String,
        messageContent: String,
        senderImgUrl: String?,
        senderUsername: String,
        messageType: MessageType
    ): Message?

    suspend fun getById(
        messageBoxId: Int,
        page: Int,
        pageSize: Int
    ): List<Message>

    suspend fun getTotalItems(
        messageBoxId: Int,
        pageSize: Int
    ): Long

    fun rowTo(row: ResultRow): Message
}