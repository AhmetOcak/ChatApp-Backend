package com.dao

import com.model.Friend
import org.jetbrains.exposed.sql.ResultRow

interface FriendDao {
    suspend fun create(
        userEmail: String,
        friendEmail: String,
        friendUsername: String,
        friendProfPicUrl: String?
    ): Friend?

    suspend fun getByEmail(userEmail: String): List<Friend>
    suspend fun updateFriend(
        userEmail: String,
        username: String?,
        profilePicUrl: String?
    )
    fun rowTo(row: ResultRow): Friend
}