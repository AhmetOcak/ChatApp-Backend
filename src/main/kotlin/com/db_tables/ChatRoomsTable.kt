package com.db_tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object ChatRoomsTable : Table() {
    val id = integer("id").autoIncrement()
    val roomName = varchar("room_name", 128)
    val roomPicUrl = varchar("room_pic_url", 256).nullable()
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }

    override val primaryKey = PrimaryKey(id)
}