package com.dao.impl

import com.dao.ChatRoomsDao
import com.db_tables.ChatRoomsTable
import com.factory.DatabaseFactory.dbQuery
import com.model.ChatRooms
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update

class ChatRoomsDaoImpl : ChatRoomsDao {
    override suspend fun create(
        roomName: String,
        roomPicUrl: String?
    ): ChatRooms? = dbQuery {
        val insertStatement = ChatRoomsTable.insert {
            it[ChatRoomsTable.roomName] = roomName
            it[ChatRoomsTable.roomPicUrl] = roomPicUrl
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::rowTo)
    }

    override suspend fun getById(id: Int): ChatRooms? {
        return dbQuery {
            ChatRoomsTable.select { ChatRoomsTable.id eq id }
                .map { rowTo(it) }
                .singleOrNull()
        }
    }

    override suspend fun updateRoom(roomId: Int, roomName: String?, roomProfilePic: String?): Boolean {
        return dbQuery {
            ChatRoomsTable.update({ ChatRoomsTable.id eq roomId }) { updateStatement ->
                roomName?.let { updateStatement[ChatRoomsTable.roomName] = it }
                roomProfilePic?.let { updateStatement[ChatRoomsTable.roomPicUrl] = it }
            } > 0
        }
    }

    override fun rowTo(row: ResultRow): ChatRooms {
        return ChatRooms(
            id = row[ChatRoomsTable.id],
            roomName = row[ChatRoomsTable.roomName],
            roomPicUrl = row[ChatRoomsTable.roomPicUrl],
            createdAt = row[ChatRoomsTable.createdAt].toString()
        )
    }
}