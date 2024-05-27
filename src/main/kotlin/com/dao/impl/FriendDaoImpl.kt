package com.dao.impl

import com.dao.FriendDao
import com.db_tables.FriendTable
import org.jetbrains.exposed.sql.ResultRow
import com.factory.DatabaseFactory.dbQuery
import com.model.Friend
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select

class FriendDaoImpl : FriendDao {
    override suspend fun create(userEmail1: String, userEmail2: String): Friend? {
        return dbQuery {
            val insertStatement = FriendTable.insert {
                it[FriendTable.user_email1] = userEmail1
                it[FriendTable.user_email2] = userEmail2
            }
            insertStatement.resultedValues?.singleOrNull()?.let(::rowTo)
        }
    }

    override suspend fun getByEmail(userEmail: String): List<Friend> {
        return dbQuery {
            FriendTable.select { (FriendTable.user_email1 eq userEmail) or (FriendTable.user_email2 eq userEmail) }
                .map { rowTo(it) }
        }
    }

    override fun rowTo(row: ResultRow): Friend {
        return Friend(
            id = row[FriendTable.id],
            userEmail1 = row[FriendTable.user_email1],
            userEmail2 = row[FriendTable.user_email2]
        )
    }
}