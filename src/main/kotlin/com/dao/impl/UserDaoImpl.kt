package com.dao.impl

import com.dao.UserDao
import com.db_tables.UserTable
import com.factory.DatabaseFactory.dbQuery
import com.model.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class UserDaoImpl() : UserDao {
    override suspend fun create(
        id: String,
        email: String,
        username: String,
        password: String,
        profilePicUrl: String?
    ): User? =
        dbQuery {
            val insertStatement = UserTable.insert {
                it[UserTable.id] = id
                it[UserTable.email] = email
                it[UserTable.username] = username
                it[UserTable.password] = password
                it[UserTable.profilePicUrl] = profilePicUrl
            }
            insertStatement.resultedValues?.singleOrNull()?.let(::rowTo)
        }

    override suspend fun getById(id: String): User? {
        return dbQuery {
            UserTable.select { UserTable.id eq id }
                .map { rowTo(it) }
                .singleOrNull()
        }
    }

    override suspend fun deleteUser(id: String): Boolean {
        return dbQuery {
            UserTable.deleteWhere { UserTable.id eq id } > 0
        }
    }

    override suspend fun updateUser(
        id: String,
        username: String?,
        password: String?,
        profilePicUrl: String?
    ): Boolean {
        return dbQuery {
            UserTable.update({ UserTable.id eq id } ) { updateStatement ->
                username?.let { updateStatement[UserTable.username] = it }
                password?.let { updateStatement[UserTable.password] = it }
                profilePicUrl?.let { updateStatement[UserTable.profilePicUrl] = it }
            } > 0
        }
    }

    override fun rowTo(row: ResultRow): User {
        return User(
            id = row[UserTable.id],
            email = row[UserTable.email],
            password = row[UserTable.password],
            username = row[UserTable.username],
            profilePicUrl = row[UserTable.profilePicUrl]
        )
    }
}