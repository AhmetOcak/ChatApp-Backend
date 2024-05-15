package com.factory

import com.db_tables.ChatRoomsTable
import com.db_tables.MessagesTable
import com.db_tables.UserTable
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        val database = Database.connect(
            url = "jdbc:postgresql://localhost:5432/chat-app",
            driver = "org.postgresql.Driver",
            user = "postgres",
            password = "ahmetocak2000"
        )
        transaction (database) {
            SchemaUtils.apply {
                create(UserTable)
                create(ChatRoomsTable)
                create(MessagesTable)
            }
        }
    }
    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}