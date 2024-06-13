package com.db_tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object FriendTable : Table() {
    val id = integer("id").autoIncrement()
    val userEmail = varchar("user_email", 128).references(UserTable.email, onDelete = ReferenceOption.CASCADE)
    val friendEmail = varchar("friend_email", 128).references(UserTable.email, onDelete = ReferenceOption.CASCADE)
    val friendUsername = varchar("friend_username", 128)
    val friendProfilePicUrl = varchar("friend_prof_pic_url", 256).nullable()

    override val primaryKey = PrimaryKey(id)

    init {
        uniqueIndex(userEmail, friendEmail)
    }
}