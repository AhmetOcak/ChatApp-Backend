package com.db_tables

import org.jetbrains.exposed.sql.Table

object FriendTable : Table() {
    val id = integer("id").autoIncrement()
    val user_email1 = varchar("user_email1", 128).references(UserTable.email)
    val user_email2 = varchar("user_email2", 128).references(UserTable.email)

    init {
        uniqueIndex(user_email1, user_email2)
    }
}