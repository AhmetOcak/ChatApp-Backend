package com.routing

import com.core.isValidEmail
import com.dao.FcmTokenDao
import com.dao.UserDao
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import java.lang.Exception
import java.lang.IllegalArgumentException

private const val BASE = "token"

fun Application.configureFcmTokenRouting(fcmTokenDao: FcmTokenDao, userDao: UserDao) {
    routing {

        post("$BASE/add") {
            try {
                val formParameters = call.receiveParameters()
                val email = formParameters.getOrFail("email")
                val token = formParameters.getOrFail("token")

                if (!email.isValidEmail()) {
                    throw IllegalArgumentException("Email is not valid")
                }

                if (token.isBlank()) {
                    throw IllegalArgumentException("Parameters must not be blank")
                }

                if (userDao.getByEmail(email) == null) {
                    throw IllegalArgumentException("There is no such a user with this email")
                }

                if (fcmTokenDao.get(email) == null) {
                    fcmTokenDao.create(email, token)
                } else fcmTokenDao.update(email, token)

                call.respond(HttpStatusCode.OK)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, message = e.stackTraceToString())
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, message = e.message ?: e.stackTraceToString())
            }
        }
    }
}