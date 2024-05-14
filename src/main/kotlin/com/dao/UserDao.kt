package com.dao

import com.model.User
import org.jetbrains.exposed.sql.ResultRow

interface UserDao {
    suspend fun create(
        id: String,
        email: String,
        username: String,
        password: String,
        profilePicUrl: String?
    ): User?

    suspend fun getById(id: String): User?
    suspend fun deleteUser(id: String): Boolean
    suspend fun updateUser(
        id: String,
        username: String?,
        password: String?,
        profilePicUrl: String?
    ): Boolean
    fun rowTo(row: ResultRow): User
}