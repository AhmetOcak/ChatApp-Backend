package com.db_tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object UserChatRoomsTable : Table() {
    val userId = varchar("user_id", 128).references(UserTable.id, onDelete = ReferenceOption.CASCADE)
    val roomId = integer("room_id").references(ChatRoomsTable.id, onDelete = ReferenceOption.CASCADE)

    override val primaryKey = PrimaryKey(userId, roomId, name = "PK_UserChatRooms")
}