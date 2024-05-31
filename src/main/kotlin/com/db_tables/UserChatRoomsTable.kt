package com.db_tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object UserChatRoomsTable : Table() {
    val userEmail = varchar("user_email", 128).references(UserTable.email, onDelete = ReferenceOption.CASCADE)
    val roomId = integer("room_id").references(ChatRoomsTable.id, onDelete = ReferenceOption.CASCADE)

    override val primaryKey = PrimaryKey(userEmail, roomId, name = "PK_UserChatRooms")
}