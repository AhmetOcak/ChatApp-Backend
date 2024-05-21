package com.dao.impl

import com.dao.UserDao
import com.db_tables.UserTable
import com.factory.DatabaseFactory.dbQuery
import com.model.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class UserDaoImpl : UserDao {
    override suspend fun create(
        email: String,
        username: String,
        profilePicUrl: String?
    ): User? =
        dbQuery {
            val insertStatement = UserTable.insert {
                it[UserTable.email] = email
                it[UserTable.username] = username
                it[UserTable.profilePicUrl] = profilePicUrl
            }
            insertStatement.resultedValues?.singleOrNull()?.let(::rowTo)
        }

    override suspend fun getByEmail(email: String): User? {
        return dbQuery {
            UserTable.select { UserTable.email eq email }
                .map { rowTo(it) }
                .singleOrNull()
        }
    }

    override suspend fun deleteUser(email: String): Boolean {
        return dbQuery {
            UserTable.deleteWhere { UserTable.email eq email } > 0
        }
    }

    override suspend fun updateUser(
        userEmail: String,
        username: String?,
        profilePicUrl: String?
    ): Boolean {
        return dbQuery {
            UserTable.update({ UserTable.email eq userEmail } ) { updateStatement ->
                username?.let { updateStatement[UserTable.username] = it }
                profilePicUrl?.let { updateStatement[UserTable.profilePicUrl] = it }
            } > 0
        }
    }

    override fun rowTo(row: ResultRow): User {
        return User(
            id = row[UserTable.id],
            email = row[UserTable.email],
            username = row[UserTable.username],
            profilePicUrl = row[UserTable.profilePicUrl]
        )
    }
}