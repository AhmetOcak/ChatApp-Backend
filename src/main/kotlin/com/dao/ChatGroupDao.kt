package com.dao

import com.model.ChatGroup
import com.model.ChatGroupParticipants
import com.model.GroupType
import org.jetbrains.exposed.sql.ResultRow

interface ChatGroupDao {
    suspend fun create(
        name: String,
        imageUrl: String?,
        creatorEmail: String,
        creatorUsername: String,
        creatorProfilePicUrl: String?,
        groupType: GroupType
    ): ChatGroup?

    suspend fun addParticipant(
        groupId: Int,
        userEmail: String,
        userName: String,
        profilePicUrl: String?
    )

    suspend fun getByEmail(email: String): List<ChatGroup>
    suspend fun getGroupParticipants(groupId: Int): List<String>
    suspend fun updateGroupParticipants(userEmail: String, userName: String?, profilePicUrl: String?)
    fun rowToParticipants(row: ResultRow): ChatGroupParticipants
}