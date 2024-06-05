package com.db_tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object MessagesTable : Table() {
    val id = integer("id").autoIncrement()
    val senderEmail = varchar("sender_email", 128).references(UserTable.email, onDelete = ReferenceOption.CASCADE)
    val receiverEmail = varchar("receiver_email", 128).references(UserTable.email, onDelete = ReferenceOption.CASCADE)
    val messageText = text("message_text")
    val senderImgUrl = varchar("sender_img_url", 256).nullable()
    val senderUsername = varchar("sender_user_name", 128)
    val sentAt = datetime("sent_at").clientDefault { LocalDateTime.now() }

    override val primaryKey = PrimaryKey(id)
}