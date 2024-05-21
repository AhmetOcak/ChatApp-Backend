package com.routing

import com.core.isValidEmail
import com.dao.impl.UserDaoImpl
import com.model.User
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.serialization.json.Json
import java.lang.IllegalArgumentException
import java.lang.Exception

private const val BASE = "user"

fun Application.configureUserRouting(userDao: UserDaoImpl) {
    routing {

        get("/$BASE/getUser/{userEmail}") {
            try {
                val userEmail = call.parameters["userEmail"] ?: return@get
                val user = userDao.getByEmail(userEmail)

                if (user == null) {
                    call.respond(HttpStatusCode.NotFound, message = "User not found.")
                } else {
                    call.respond(HttpStatusCode.OK, Json.encodeToString(User.serializer(), user))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, message = e.stackTraceToString())
            }
        }

        post("/$BASE/createUser") {
            try {
                val formParameters = call.receiveParameters()
                val username = formParameters.getOrFail("username")
                val email = formParameters.getOrFail("email")
                val profilePicUrl = formParameters["profilePicUrl"]

                if (username.isBlank() || email.isBlank() || profilePicUrl?.isBlank() == true) {
                    throw IllegalArgumentException("Parameters must not be blank")
                }
                if (!email.isValidEmail()) {
                    throw IllegalArgumentException("Email is not valid")
                }

                val user = userDao.create(
                    username = username,
                    email = email,
                    profilePicUrl = profilePicUrl
                )

                if (user == null) {
                    throw Exception("Error occurred while creating user.")
                }

                call.respond(HttpStatusCode.Created, message = user)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, message = e.stackTraceToString())
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, message = e.message ?: e.stackTraceToString())
            }
        }

        delete("/$BASE/deleteUser/{userEmail}") {
            try {
                val userEmail = call.parameters["userEmail"] ?: return@delete
                val isDeleted = userDao.deleteUser(userEmail)

                if (isDeleted) {
                    call.respond(HttpStatusCode.OK, message = "User deleted.")
                } else {
                    call.respond(HttpStatusCode.NotFound, message = "User not found.")
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, message = e.stackTraceToString())
            }
        }

        put("/$BASE/updateUser/{userEmail}") {
            try {
                val userEmail = call.parameters["userEmail"] ?: return@put

                val formParameters = call.receiveParameters()
                val username = formParameters["username"]
                val profilePicUrl = formParameters["profilePicUrl"]

                if (username?.isBlank() == true || profilePicUrl?.isBlank() == true) {
                    throw IllegalArgumentException("Parameters must not be blank")
                }

                val isUpdated = userDao.updateUser(
                    userEmail = userEmail,
                    username = username,
                    profilePicUrl = profilePicUrl
                )

                if (isUpdated) {
                    call.respond(HttpStatusCode.OK, message = "User updated successfully.")
                } else {
                    call.respond(HttpStatusCode.InternalServerError, message = "User update failed.")
                }
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, message = e.message ?: e.stackTraceToString())
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, message = e.stackTraceToString())
            }
        }
    }
}
