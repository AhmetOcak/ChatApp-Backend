package com.dao.impl

import com.dao.MessagesDao
import com.db_tables.MessagesTable
import com.factory.DatabaseFactory.dbQuery
import com.model.Message
import com.model.MessageType
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.lang.IllegalArgumentException

class MessagesDaoImpl : MessagesDao {
    override suspend fun create(
        friendshipId: Int,
        senderEmail: String,
        receiverEmail: String,
        messageContent: String,
        senderImgUrl: String?,
        senderUsername: String,
        messageType: MessageType
    ): Message? = dbQuery {
        val insertStatement = MessagesTable.insert {
            it[MessagesTable.friendshipId] = friendshipId
            it[MessagesTable.senderEmail] = senderEmail
            it[MessagesTable.receiverEmail] = receiverEmail
            it[MessagesTable.messageContent] = messageContent
            it[MessagesTable.senderImgUrl] = senderImgUrl
            it[MessagesTable.senderUsername] = senderUsername
            it[MessagesTable.messageType] = messageType.name
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::rowTo)
    }

    override suspend fun getById(
        friendshipId: Int,
        page: Int,
        pageSize: Int
    ): List<Message> {
        return dbQuery {
            MessagesTable.select(
                MessagesTable.friendshipId eq friendshipId
            ).orderBy(MessagesTable.sentAt to SortOrder.DESC)
                .limit(pageSize, offset = (page * pageSize).toLong())
                .map { rowTo(it) }
        }
    }

    override suspend fun getTotalItems(
        friendshipId: Int,
        pageSize: Int
    ): Long {
        return dbQuery {
            val count = MessagesTable.select(
                MessagesTable.friendshipId eq friendshipId
            ).count()
            count / pageSize
        }
    }

    override fun rowTo(row: ResultRow): Message {
        return Message(
            id = row[MessagesTable.id],
            friendshipId = row[MessagesTable.friendshipId],
            senderEmail = row[MessagesTable.senderEmail],
            receiverEmail = row[MessagesTable.receiverEmail],
            messageContent = row[MessagesTable.messageContent],
            sentAt = row[MessagesTable.sentAt].toString(),
            senderImgUrl = row[MessagesTable.senderImgUrl],
            senderUsername = row[MessagesTable.senderUsername],
            messageType = row[MessagesTable.messageType].toMessageType()
        )
    }
}

private fun String.toMessageType(): MessageType {
    return when (this.uppercase()) {
        MessageType.TEXT.name -> MessageType.TEXT
        MessageType.AUDIO.name -> MessageType.AUDIO
        MessageType.IMAGE.name -> MessageType.IMAGE
        MessageType.DOC.name -> MessageType.DOC
        else -> throw IllegalArgumentException("Wrong message type $this")
    }
}