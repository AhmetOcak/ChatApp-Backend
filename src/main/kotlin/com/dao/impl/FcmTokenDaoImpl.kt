package com.dao.impl

import com.dao.FcmTokenDao
import com.db_tables.FcmTokenTable
import com.factory.DatabaseFactory.dbQuery
import com.model.FcmToken
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class FcmTokenDaoImpl : FcmTokenDao {
    override suspend fun create(email: String, token: String) {
        dbQuery {
            FcmTokenTable.insert {
                it[FcmTokenTable.email] = email
                it[FcmTokenTable.token] = token
            }
        }
    }

    override suspend fun delete(email: String) {
        dbQuery { FcmTokenTable.deleteWhere { FcmTokenTable.email eq email } }
    }

    override suspend fun update(email: String, token: String) {
        dbQuery {
            FcmTokenTable.update({ FcmTokenTable.email eq email }) {
                it[FcmTokenTable.token] = token
            }
        }
    }

    override suspend fun get(email: String): String? {
        return dbQuery {
            FcmTokenTable.select {
                FcmTokenTable.email eq email
            }.map { rowTo(it) }
                .singleOrNull()?.token
        }
    }

    override suspend fun rowTo(row: ResultRow): FcmToken {
        return FcmToken(
            id = row[FcmTokenTable.id],
            email = row[FcmTokenTable.email],
            token = row[FcmTokenTable.token]
        )
    }
}