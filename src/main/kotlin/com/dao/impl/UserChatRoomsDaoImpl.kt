package com.dao.impl

import com.dao.UserChatRoomsDao
import com.db_tables.UserChatRoomsTable
import com.factory.DatabaseFactory.dbQuery
import com.model.UserChatRooms
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class UserChatRoomsDaoImpl : UserChatRoomsDao {
    override suspend fun addUserToRoom(userEmail: String, roomId: Int): Int? = dbQuery {
        val insertStatement = UserChatRoomsTable.insert {
            it[UserChatRoomsTable.userEmail] = userEmail
            it[UserChatRoomsTable.roomId] = roomId
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::rowTo)?.roomId
    }

    override suspend fun removeUserFromRoom(userEmail: String, roomId: Int): Boolean {
        return dbQuery {
            UserChatRoomsTable.deleteWhere {
                (UserChatRoomsTable.userEmail eq userEmail) and (UserChatRoomsTable.roomId eq roomId)
            } > 0
        }
    }

    override suspend fun getUserRooms(userEmail: String): List<Int> {
        return dbQuery {
            UserChatRoomsTable.select { UserChatRoomsTable.userEmail eq userEmail }
                .map { it[UserChatRoomsTable.roomId] }
        }
    }

    override fun rowTo(row: ResultRow): UserChatRooms {
        return UserChatRooms(
            userEmail = row[UserChatRoomsTable.userEmail],
            roomId = row[UserChatRoomsTable.roomId]
        )
    }
}