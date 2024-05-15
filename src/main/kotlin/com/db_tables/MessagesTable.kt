package com.db_tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object MessagesTable : Table() {
    val id = integer("id").autoIncrement()
    val senderId = varchar("sender_id", 128).references(UserTable.id, onDelete = ReferenceOption.CASCADE)
    val senderProfilePicUrl = varchar("sender_profile_pic_url", 256).nullable()
    val roomId = integer("room_id").references(ChatRoomsTable.id, onDelete = ReferenceOption.CASCADE)
    val messageText = text("message_text")
    val sentAt = datetime("sent_at").clientDefault { LocalDateTime.now() }

    override val primaryKey = PrimaryKey(id)
}