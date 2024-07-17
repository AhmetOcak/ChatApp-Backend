package com.db_tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object ChatGroupTable : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 128)
    val imageUrl = varchar("image", 256).nullable()
    val groupType = varchar("group_type", 32)

    override val primaryKey = PrimaryKey(id)
}

object ChatGroupParticipantsTable : Table() {
    val groupId = integer("group_id").references(ChatGroupTable.id, onDelete = ReferenceOption.CASCADE)
    val participantEmail = varchar("participant_email", 128)
    val participantUsername = varchar("participant_username", 128)
    val participantProfilePicUrl = varchar("participant_profile_pic", 256).nullable()

    init {
        uniqueIndex(groupId, participantEmail)
    }
}

