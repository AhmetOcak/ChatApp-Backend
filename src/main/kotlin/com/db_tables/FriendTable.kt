package com.db_tables

import org.jetbrains.exposed.sql.Table

object FriendTable : Table() {
    val id = integer("id").autoIncrement()
    val userEmail = varchar("user_email", 128).references(UserTable.email)
    val friendEmail = varchar("friend_email", 128).references(UserTable.email)
    val friendProfilePicUrl = varchar("friend_prof_pic_url", 256).nullable()

    init {
        uniqueIndex(userEmail, friendEmail)
    }
}