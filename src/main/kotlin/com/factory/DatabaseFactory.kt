package com.factory

import com.db_tables.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        val database = Database.connect(
            url = System.getenv("DB_URL"),
            driver = "org.postgresql.Driver",
            user = System.getenv("DB_USER"),
            password = System.getenv("DB_PASSWORD")
        )
        transaction (database) {
            SchemaUtils.apply {
                create(UserTable)
                create(MessagesTable)
                create(FcmTokenTable)
                create(ChatGroupTable)
                create(ChatGroupParticipantsTable)
            }
        }
    }
    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}