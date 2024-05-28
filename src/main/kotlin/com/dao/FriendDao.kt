package com.dao

import com.model.Friend
import org.jetbrains.exposed.sql.ResultRow

interface FriendDao {
    suspend fun create(userEmail: String, friendEmail: String, friendProfPicUrl: String?): Friend?
    suspend fun getByEmail(userEmail: String): List<Friend>
    fun rowTo(row: ResultRow): Friend
}