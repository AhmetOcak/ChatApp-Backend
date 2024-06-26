package com.db_tables

import org.jetbrains.exposed.sql.Table

object UserTable : Table() {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 128)
    val email = varchar("email", 128).uniqueIndex()
    val profilePicUrl = varchar("profile_pic_url", 256).nullable()

    override val primaryKey = PrimaryKey(id)
}