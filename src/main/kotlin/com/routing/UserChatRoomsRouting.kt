package com.routing

import com.dao.UserChatRoomsDao
import com.model.ChatRooms
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private const val BASE = "userChatRooms"

fun Application.configureUserChatRoomsRouting(
    userChatRoomsDao: UserChatRoomsDao,
    getRoom: suspend (Int) -> ChatRooms?
) {
    routing {

        post("/$BASE/addUserToRoom/{userEmail}/{roomId}") {
            try {
                val userEmail = call.parameters["userEmail"] ?: return@post
                val roomId = call.parameters["roomId"]?.toInt() ?: return@post

                val userRoomId = userChatRoomsDao.addUserToRoom(userEmail = userEmail, roomId = roomId)

                if (userRoomId != null) {
                    val chatRoom = getRoom(userRoomId)
                    call.respond(HttpStatusCode.Created, chatRoom!!)
                } else {
                    call.respond(HttpStatusCode.InternalServerError, message = "User could not be added to chat room.")
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, e.stackTraceToString())
            }
        }

        put("/$BASE/removeUserFromRoom/{userEmail}/{roomId}") {
            try {
                val userEmail = call.parameters["userEmail"] ?: return@put
                val roomId = call.parameters["roomId"]?.toInt() ?: return@put

                val isRemoved = userChatRoomsDao.removeUserFromRoom(userEmail = userEmail, roomId = roomId)

                if (isRemoved) {
                    call.respond(HttpStatusCode.OK, message = "User removed from chat room successfully.")
                } else {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        message = "User could not be removed from chat room."
                    )
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, e.stackTraceToString())
            }
        }

        get("/$BASE/getUserRooms/{userEmail}") {
            try {
                val userEmail = call.parameters["userEmail"] ?: return@get
                val userRooms = userChatRoomsDao.getUserRooms(userEmail)

                call.respond(HttpStatusCode.OK, userRooms)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, e.stackTraceToString())
            }
        }
    }
}