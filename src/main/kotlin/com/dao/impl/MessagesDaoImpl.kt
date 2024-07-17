package com.dao.impl

import com.core.toMessageType
import com.dao.MessagesDao
import com.db_tables.MessagesTable
import com.factory.DatabaseFactory.dbQuery
import com.model.Message
import com.model.MessageType
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class MessagesDaoImpl : MessagesDao {
    override suspend fun create(
        messageBoxId: Int,
        senderEmail: String,
        messageContent: String,
        senderImgUrl: String?,
        senderUsername: String,
        messageType: MessageType
    ): Message? = dbQuery {
        val insertStatement = MessagesTable.insert {
            it[MessagesTable.messageBoxId] = messageBoxId
            it[MessagesTable.senderEmail] = senderEmail
            it[MessagesTable.messageContent] = messageContent
            it[MessagesTable.senderImgUrl] = senderImgUrl
            it[MessagesTable.senderUsername] = senderUsername
            it[MessagesTable.messageType] = messageType.name
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::rowTo)
    }

    override suspend fun getById(
        messageBoxId: Int,
        page: Int,
        pageSize: Int
    ): List<Message> {
        return dbQuery {
            MessagesTable.select(
                MessagesTable.messageBoxId eq messageBoxId
            ).orderBy(MessagesTable.sentAt to SortOrder.DESC)
                .limit(pageSize, offset = (page * pageSize).toLong())
                .map { rowTo(it) }
        }
    }

    override suspend fun getTotalItems(
        messageBoxId: Int,
        pageSize: Int
    ): Long {
        return dbQuery {
            val count = MessagesTable.select(
                MessagesTable.messageBoxId eq messageBoxId
            ).count()
            count / pageSize
        }
    }

    override fun rowTo(row: ResultRow): Message {
        return Message(
            id = row[MessagesTable.id],
            messageBoxId = row[MessagesTable.messageBoxId],
            senderEmail = row[MessagesTable.senderEmail],
            messageContent = row[MessagesTable.messageContent],
            sentAt = row[MessagesTable.sentAt].toString(),
            senderImgUrl = row[MessagesTable.senderImgUrl],
            senderUsername = row[MessagesTable.senderUsername],
            messageType = row[MessagesTable.messageType].toMessageType()
        )
    }
}

