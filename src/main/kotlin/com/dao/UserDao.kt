package com.dao

import com.model.User
import org.jetbrains.exposed.sql.ResultRow

interface UserDao {
    suspend fun create(
        email: String,
        username: String,
        profilePicUrl: String?
    ): User?

    suspend fun getByEmail(email: String): User?
    suspend fun deleteUser(id: Int): Boolean
    suspend fun updateUser(
        id: Int,
        username: String?,
        profilePicUrl: String?
    ): Boolean
    fun rowTo(row: ResultRow): User
}