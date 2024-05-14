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

        get("/$BASE/getUser/{userId}") {
            try {
                val userId = call.parameters["userId"] ?: return@get
                val user = userDao.getById(userId)

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
                val id = formParameters.getOrFail("id")
                val username = formParameters.getOrFail("username")
                val email = formParameters.getOrFail("email")
                val password = formParameters.getOrFail("password")
                val profilePicUrl = formParameters.getOrFail("profilePicUrl")

                if (id.isBlank() || username.isBlank() || email.isBlank() || password.isBlank() || profilePicUrl.isBlank()) {
                    throw IllegalArgumentException("Parameters must not be blank")
                }
                if (!email.isValidEmail()) {
                    throw IllegalArgumentException("Email is not valid")
                }

                val user = userDao.create(
                    id = id,
                    username = username,
                    email = email,
                    password = password,
                    profilePicUrl = profilePicUrl
                )

                if (user == null) {
                    throw Exception("Error occurred while creating user.")
                }

                call.respond(HttpStatusCode.Created, message = "New user created.")
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, message = e.stackTraceToString())
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, message = e.message ?: e.stackTraceToString())
            }
        }

        delete("/$BASE/deleteUser/{userId}") {
            try {
                val userId = call.parameters["userId"] ?: return@delete
                val isDeleted = userDao.deleteUser(userId)

                if (isDeleted) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound, message = "User not found.")
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, message = e.stackTraceToString())
            }
        }

        put("/$BASE/updateUser/{userId}") {
            try {
                val id = call.parameters["userId"] ?: return@put

                val formParameters = call.receiveParameters()
                val username = formParameters["username"]
                val password = formParameters["password"]
                val profilePicUrl = formParameters["profilePicUrl"]

                if (username?.isBlank() == true || password?.isBlank() == true || profilePicUrl?.isBlank() == true) {
                    throw IllegalArgumentException("Parameters must not be blank")
                }

                val isUpdated = userDao.updateUser(
                    id = id,
                    username = username,
                    password = password,
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
