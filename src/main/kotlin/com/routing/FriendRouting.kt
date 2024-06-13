package com.routing

import com.core.isValidEmail
import com.dao.FriendDao
import com.dao.UserDao
import com.model.Friend
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import java.lang.IllegalArgumentException

private const val BASE = "friend"

fun Application.configureFriendRouting(friendDao: FriendDao, userDao: UserDao) {
    routing {

        post("/$BASE/create") {
            try {
                val formParameters = call.receiveParameters()
                val currentUserEmail = formParameters.getOrFail("currentUserEmail")
                val friendEmail = formParameters.getOrFail("friendEmail")

                if (!currentUserEmail.isValidEmail() || !friendEmail.isValidEmail()) {
                    throw IllegalArgumentException("Email is not valid")
                }

                val friendAccount = userDao.getByEmail(friendEmail)

                val friend = friendDao.create(
                    userEmail = currentUserEmail,
                    friendEmail = friendEmail,
                    friendProfPicUrl = friendAccount?.profilePicUrl,
                    friendUsername = friendAccount?.username ?: ""
                )

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

                if (friendList.firstOrNull()?.friendEmail == userEmail) {
                    val reFriendList = friendList.map {
                        Friend(
                            id = it.id,
                            userEmail = userEmail,
                            friendEmail = it.userEmail,
                            friendProfilePicUrl = it.userEmail?.let { it1 -> userDao.getByEmail(it1)?.profilePicUrl },
                            friendUsername = it.userEmail?.let { it1 -> userDao.getByEmail(it1)?.username }
                        )
                    }
                    call.respond(HttpStatusCode.OK, message = reFriendList)
                }

                call.respond(HttpStatusCode.OK, message = friendList)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, message = e.stackTraceToString())
            }
        }
    }
}