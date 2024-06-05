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
    override suspend fun create(
        userEmail: String,
        friendEmail: String,
        friendUsername: String,
        friendProfPicUrl: String?
    ): Friend? {
        return dbQuery {
            val insertStatement = FriendTable.insert {
                it[FriendTable.userEmail] = userEmail
                it[FriendTable.friendEmail] = friendEmail
                it[FriendTable.friendUsername] = friendUsername
                it[FriendTable.friendProfilePicUrl] = friendProfPicUrl
            }
            insertStatement.resultedValues?.singleOrNull()?.let(::rowTo)
        }
    }

    override suspend fun getByEmail(userEmail: String): List<Friend> {
        return dbQuery {
            FriendTable.select { (FriendTable.userEmail eq userEmail) or (FriendTable.friendEmail eq userEmail) }
                .map { rowTo(it) }
        }
    }

    override fun rowTo(row: ResultRow): Friend {
        return Friend(
            id = row[FriendTable.id],
            userEmail = row[FriendTable.userEmail],
            friendEmail = row[FriendTable.friendEmail],
            friendProfilePicUrl = row[FriendTable.friendProfilePicUrl],
            friendUsername = row[FriendTable.friendUsername]
        )
    }
}