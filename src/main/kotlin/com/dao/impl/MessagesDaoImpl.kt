package com.dao.impl

import com.dao.MessagesDao
import com.db_tables.MessagesTable
import com.factory.DatabaseFactory.dbQuery
import com.model.Message
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class MessagesDaoImpl : MessagesDao {
    override suspend fun create(
        senderEmail: String,
        receiverEmail: String,
        messageText: String,
        senderImgUrl: String?
    ): Message? = dbQuery {
        val insertStatement = MessagesTable.insert {
            it[MessagesTable.senderEmail] = senderEmail
            it[MessagesTable.receiverEmail] = receiverEmail
            it[MessagesTable.messageText] = messageText
            it[MessagesTable.senderImgUrl] = senderImgUrl
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::rowTo)
    }

    override suspend fun getById(
        senderEmail: String,
        receiverEmail: String,
        page: Int,
        pageSize: Int
    ): List<Message> {
        return dbQuery {
            MessagesTable.select(
                ((MessagesTable.senderEmail eq senderEmail) and (MessagesTable.receiverEmail eq receiverEmail))
                        or ((MessagesTable.senderEmail eq receiverEmail) and (MessagesTable.receiverEmail eq senderEmail))
            ).orderBy(MessagesTable.sentAt to SortOrder.DESC)
                .limit(pageSize, offset = (page * pageSize).toLong())
                .map { rowTo(it) }
        }
    }

    override suspend fun getTotalItems(
        senderEmail: String,
        receiverEmail: String,
        pageSize: Int
    ): Long {
        return dbQuery {
            val count = MessagesTable.select {
                (MessagesTable.senderEmail eq senderEmail) and (MessagesTable.receiverEmail eq receiverEmail)
            }.union(
                MessagesTable.select {
                    (MessagesTable.senderEmail eq receiverEmail) and (MessagesTable.receiverEmail eq senderEmail)
                }
            ).count()
            count / pageSize
        }
    }

    override fun rowTo(row: ResultRow): Message {
        return Message(
            id = row[MessagesTable.id],
            senderEmail = row[MessagesTable.senderEmail],
            receiverEmail = row[MessagesTable.receiverEmail],
            messageText = row[MessagesTable.messageText],
            sentAt = row[MessagesTable.sentAt].toString(),
            senderImgUrl = row[MessagesTable.senderImgUrl]
        )
    }
}