package com.db_tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object MessagesTable : Table() {
    val id = integer("id").autoIncrement()
    val friendshipId = integer("friendship_id")
    val senderEmail = varchar("sender_email", 128)
    val receiverEmail = varchar("receiver_email", 128)
    val messageContent = text("message_content")
    val senderImgUrl = varchar("sender_img_url", 256).nullable()
    val senderUsername = varchar("sender_user_name", 128)
    val sentAt = datetime("sent_at").clientDefault { LocalDateTime.now() }
    val messageType = varchar("message_type", 32)

    override val primaryKey = PrimaryKey(id)
}