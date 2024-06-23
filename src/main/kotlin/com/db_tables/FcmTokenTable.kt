package com.db_tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object FcmTokenTable : Table() {
    val id = integer("id").autoIncrement()
    val email = varchar("email", 128).references(UserTable.email, onDelete = ReferenceOption.CASCADE)
    val token = varchar("token", 256)

    override val primaryKey = PrimaryKey(id)
}