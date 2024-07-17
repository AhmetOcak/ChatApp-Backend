package com.dao.impl

import com.core.toGroupType
import com.dao.ChatGroupDao
import com.db_tables.ChatGroupParticipantsTable
import com.db_tables.ChatGroupTable
import com.factory.DatabaseFactory.dbQuery
import com.model.ChatGroup
import com.model.ChatGroupParticipants
import com.model.GroupType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update

class ChatGroupDaoImpl : ChatGroupDao {
    override suspend fun create(
        name: String,
        imageUrl: String?,
        creatorEmail: String,
        creatorUsername: String,
        creatorProfilePicUrl: String?,
        groupType: GroupType
    ): ChatGroup? {
        return dbQuery {
            val insertStatement = ChatGroupTable.insert {
                it[ChatGroupTable.name] = name
                it[ChatGroupTable.imageUrl] = imageUrl
                it[ChatGroupTable.groupType] = groupType.name
            }
            val group = insertStatement.resultedValues?.singleOrNull()?.let {
                ChatGroup(
                    id = it[ChatGroupTable.id],
                    name = it[ChatGroupTable.name],
                    imageUrl = it[ChatGroupTable.imageUrl],
                    participants = listOf(
                        ChatGroupParticipants(
                            id = it[ChatGroupTable.id],
                            participantEmail = creatorEmail,
                            participantUsername = creatorUsername,
                            participantProfilePicUrl = creatorProfilePicUrl
                        )
                    ),
                    groupType = it[ChatGroupTable.groupType].toGroupType()
                )
            }
            group?.let { chatGroup ->
                ChatGroupParticipantsTable.insert {
                    it[groupId] = chatGroup.id
                    it[participantEmail] = creatorEmail
                    it[participantUsername] = creatorUsername
                    it[participantProfilePicUrl] = creatorProfilePicUrl
                }
            }
            group
        }
    }

    override suspend fun addParticipant(
        groupId: Int,
        userEmail: String,
        userName: String,
        profilePicUrl: String?
    ) {
        return dbQuery {
            ChatGroupParticipantsTable.insert {
                it[ChatGroupParticipantsTable.groupId] = groupId
                it[participantEmail] = userEmail
                it[participantUsername] = userName
                it[participantProfilePicUrl] = profilePicUrl
            }
        }
    }

    override suspend fun getByEmail(email: String): List<ChatGroup> {
        return dbQuery {
            val userChatGroups = mutableListOf<ChatGroup>()
            val groupIds = ChatGroupParticipantsTable.select {
                ChatGroupParticipantsTable.participantEmail eq email
            }.map { it[ChatGroupParticipantsTable.groupId] }

            if (groupIds.isNotEmpty()) {
                groupIds.forEach { id ->
                    ChatGroupTable.select { ChatGroupTable.id eq id }.map {
                        ChatGroup(
                            id = id,
                            name = it[ChatGroupTable.name],
                            imageUrl = it[ChatGroupTable.imageUrl],
                            participants = ChatGroupParticipantsTable.select {
                                ChatGroupParticipantsTable.groupId eq id
                            }.map { participantTable ->
                                ChatGroupParticipants(
                                    id = id,
                                    participantEmail = participantTable[ChatGroupParticipantsTable.participantEmail],
                                    participantUsername = participantTable[ChatGroupParticipantsTable.participantUsername],
                                    participantProfilePicUrl = participantTable[ChatGroupParticipantsTable.participantProfilePicUrl]
                                )
                            },
                            groupType = it[ChatGroupTable.groupType].toGroupType()
                        )
                    }.singleOrNull()?.let { chatGroup ->
                        userChatGroups.add(chatGroup)
                    }
                }
                userChatGroups
            } else emptyList()
        }
    }

    override suspend fun getGroupParticipants(groupId: Int): List<String> {
        return dbQuery {
             ChatGroupParticipantsTable.select { ChatGroupParticipantsTable.groupId eq groupId }.map {
                it[ChatGroupParticipantsTable.participantEmail]
            }
        }
    }

    override suspend fun updateGroupParticipants(
        userEmail: String,
        userName: String?,
        profilePicUrl: String?
    ) {
        dbQuery {
            ChatGroupParticipantsTable.update(
                { ChatGroupParticipantsTable.participantEmail eq userEmail }
            ) { updateStatement ->
                userName?.let { updateStatement[participantUsername] = it }
                profilePicUrl?.let { updateStatement[participantProfilePicUrl] = it }
            }
        }
    }

    override suspend fun isPrivateGroupExist(userEmail: String, friendEmail: String): Boolean {
        return dbQuery {
            val privateGroups = ChatGroupTable.select {
                ChatGroupTable.groupType eq GroupType.PRIVATE_CHAT_GROUP.name
            }.map {
                ChatGroup(
                    id = it[ChatGroupTable.id],
                    name = it[ChatGroupTable.name],
                    groupType = it[ChatGroupTable.groupType].toGroupType(),
                    participants = ChatGroupParticipantsTable.select {
                        ChatGroupParticipantsTable.groupId eq it[ChatGroupTable.id]
                    }.map { participantTable ->
                        ChatGroupParticipants(
                            id = it[ChatGroupTable.id],
                            participantEmail = participantTable[ChatGroupParticipantsTable.participantEmail],
                            participantUsername = participantTable[ChatGroupParticipantsTable.participantUsername],
                            participantProfilePicUrl = participantTable[ChatGroupParticipantsTable.participantProfilePicUrl]
                        )
                    },
                    imageUrl = it[ChatGroupTable.imageUrl]
                )
            }

            val userGroupsIds: MutableSet<Int> = mutableSetOf()
            val friendGroupsIds: MutableSet<Int> = mutableSetOf()

            privateGroups.map { group ->
                group.participants.firstOrNull { it.participantEmail == userEmail }?.let {
                    userGroupsIds.add(it.id)
                }
            }
            privateGroups.map { group ->
                group.participants.firstOrNull { it.participantEmail == friendEmail }?.let {
                    friendGroupsIds.add(it.id)
                }
            }

            userGroupsIds.any { it in friendGroupsIds }
        }
    }

    override fun rowToParticipants(row: ResultRow): ChatGroupParticipants {
        return ChatGroupParticipants(
            id = row[ChatGroupParticipantsTable.groupId],
            participantEmail = row[ChatGroupParticipantsTable.participantEmail],
            participantUsername = row[ChatGroupParticipantsTable.participantUsername],
            participantProfilePicUrl = row[ChatGroupParticipantsTable.participantProfilePicUrl]
        )
    }
}