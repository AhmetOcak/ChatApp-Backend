package com.dao

import com.model.FcmToken
import org.jetbrains.exposed.sql.ResultRow

interface FcmTokenDao {
    suspend fun create(email: String, token: String)
    suspend fun delete(email: String)
    suspend fun update(email: String, token: String)
    suspend fun get(email: String): String?
    suspend fun rowTo(row: ResultRow): FcmToken
}