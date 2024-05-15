package com.dao.impl

import com.dao.MessagesDao
import com.db_tables.MessagesTable
import com.factory.DatabaseFactory.dbQuery
import com.model.Message
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class MessagesDaoImpl : MessagesDao {
    override suspend fun create(
        senderId: Int,
        senderProfilePicUrl: String?,
        roomId: Int,
        messageText: String
    ): Message? = dbQuery {
        val insertStatement = MessagesTable.insert {
            it[MessagesTable.senderId] = senderId
            it[MessagesTable.senderProfilePicUrl] = senderProfilePicUrl
            it[MessagesTable.roomId] = roomId
            it[MessagesTable.messageText] = messageText
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::rowTo)
    }

    override suspend fun getById(roomId: Int, page: Int, pageSize: Int): List<Message> {
        return dbQuery {
            MessagesTable.select { MessagesTable.roomId eq roomId }
                .limit(pageSize, offset = (page * pageSize).toLong())
                .map { rowTo(it) }
        }
    }

    override suspend fun getTotalItems(roomId: Int, pageSize: Int): Long {
        return dbQuery {
            val count = MessagesTable.select { MessagesTable.roomId eq roomId }.count()
            count / pageSize
        }
    }

    override fun rowTo(row: ResultRow): Message {
        return Message(
            id = row[MessagesTable.id],
            senderId = row[MessagesTable.senderId],
            senderProfilePicUrl = row[MessagesTable.senderProfilePicUrl],
            roomId = row[MessagesTable.roomId],
            messageText = row[MessagesTable.messageText],
            sentAt = row[MessagesTable.sentAt].toString()
        )
    }
}