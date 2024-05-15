package com.dao

import com.model.User
import org.jetbrains.exposed.sql.ResultRow

interface UserDao {
    suspend fun create(
        email: String,
        username: String,
        password: String,
        profilePicUrl: String?
    ): User?

    suspend fun getById(id: Int): User?
    suspend fun deleteUser(id: Int): Boolean
    suspend fun updateUser(
        id: Int,
        username: String?,
        password: String?,
        profilePicUrl: String?
    ): Boolean
    fun rowTo(row: ResultRow): User
}