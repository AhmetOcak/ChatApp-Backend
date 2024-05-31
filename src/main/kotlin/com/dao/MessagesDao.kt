package com.dao

import com.model.Message
import org.jetbrains.exposed.sql.ResultRow

interface MessagesDao {
    suspend fun create(
        senderEmail: String,
        receiverEmail: String,
        messageText: String
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