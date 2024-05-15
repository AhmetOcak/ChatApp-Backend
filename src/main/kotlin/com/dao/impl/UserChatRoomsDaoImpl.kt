package com.dao.impl

import com.dao.UserChatRoomsDao
import com.db_tables.UserChatRoomsTable
import com.factory.DatabaseFactory.dbQuery
import com.model.UserChatRooms
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class UserChatRoomsDaoImpl : UserChatRoomsDao {
    override suspend fun addUserToRoom(userId: Int, roomId: Int): Int? = dbQuery {
        val insertStatement = UserChatRoomsTable.insert {
            it[UserChatRoomsTable.userId] = userId
            it[UserChatRoomsTable.roomId] = roomId
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::rowTo)?.roomId
    }

    override suspend fun removeUserFromRoom(userId: Int, roomId: Int): Boolean {
        return dbQuery {
            UserChatRoomsTable.deleteWhere {
                (UserChatRoomsTable.userId eq userId) and (UserChatRoomsTable.roomId eq roomId)
            } > 0
        }
    }

    override suspend fun getUserRooms(userId: Int): List<Int> {
        return dbQuery {
            UserChatRoomsTable.select { UserChatRoomsTable.userId eq userId }
                .map { it[UserChatRoomsTable.roomId] }
        }
    }

    override fun rowTo(row: ResultRow): UserChatRooms {
        return UserChatRooms(
            userId = row[UserChatRoomsTable.userId],
            roomId = row[UserChatRoomsTable.roomId]
        )
    }
}