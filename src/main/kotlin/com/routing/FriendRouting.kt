package com.routing

import com.core.isValidEmail
import com.dao.FriendDao
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import java.lang.IllegalArgumentException

private const val BASE = "friend"

fun Application.configureFriendRouting(friendDao: FriendDao) {
    routing {

        post("/$BASE/create") {
            try {
                val formParameters = call.receiveParameters()
                val userEmail1 = formParameters.getOrFail("currentUserEmail")
                val userEmail2 = formParameters.getOrFail("friendEmail")

                if (!userEmail1.isValidEmail() || !userEmail2.isValidEmail()) {
                    throw IllegalArgumentException("Email is not valid")
                }

                val friend = friendDao.create(userEmail1 = userEmail1, userEmail2 = userEmail2)

                if (friend == null) {
                    throw java.lang.Exception("Error occurred while creating friend.")
                }

                call.respond(HttpStatusCode.Created, message = friend)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, message = e.stackTraceToString())
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, message = e.stackTraceToString())
            }
        }

        get("$BASE/getFriends/{userEmail}") {
            try {
                val userEmail = call.parameters["userEmail"] ?: return@get
                val friendList = friendDao.getByEmail(userEmail)

                call.respond(HttpStatusCode.OK, message = friendList)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, message = e.stackTraceToString())
            }
        }
    }
}