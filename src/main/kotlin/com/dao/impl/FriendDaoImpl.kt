package com.dao.impl

import com.dao.FriendDao
import com.db_tables.FriendTable
import com.factory.DatabaseFactory.dbQuery
import com.model.Friend
import org.jetbrains.exposed.sql.*

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

    override suspend fun updateFriend(
        userEmail: String,
        username: String?,
        profilePicUrl: String?
    ) {
        dbQuery {
            FriendTable.update ({
                (FriendTable.userEmail eq userEmail) or (FriendTable.friendEmail eq userEmail) }
            ) { updateStatement ->
                username?.let { updateStatement[FriendTable.friendUsername] = it }
                profilePicUrl?.let { updateStatement[FriendTable.friendProfilePicUrl] = it }
            }
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