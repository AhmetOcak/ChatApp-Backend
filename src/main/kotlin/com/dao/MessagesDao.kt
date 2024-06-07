package com.dao

import com.model.Message
import com.model.MessageType
import org.jetbrains.exposed.sql.ResultRow

interface MessagesDao {
    suspend fun create(
        senderEmail: String,
        receiverEmail: String,
        messageContent: String,
        senderImgUrl: String?,
        senderUsername: String,
        messageType: MessageType
    ): Message?

    suspend fun getById(
        senderEmail: String,
        receiverEmail: String,
        page: Int,
        pageSize: Int
    ): List<Message>

    suspend fun getTotalItems(
        senderEmail: String,
        receiverEmail: String,
        pageSize: Int
    ): Long

    fun rowTo(row: ResultRow): Message
}