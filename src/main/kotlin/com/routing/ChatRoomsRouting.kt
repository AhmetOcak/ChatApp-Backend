package com.routing

import com.core.isValidEmail
import com.dao.impl.ChatRoomsDaoImpl
import com.model.User
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import java.lang.IllegalArgumentException

private const val BASE = "chatRoom"

fun Application.configureChatRooms(
    chatRoomDao: ChatRoomsDaoImpl,
    addUserToChatRoom: suspend (String, Int) -> Unit,
    getUser: suspend (String) -> User?
) {
    routing {
        post("/$BASE/createChatRoom") {
            try {
                val formParameters = call.receiveParameters()
                val roomName = formParameters.getOrFail("roomName")
                val userEmail = formParameters.getOrFail("userEmail")
                val participants = formParameters.getOrFail("participants").toList()
                val roomPicUrl = formParameters["roomPicUrl"]

                if (roomName.isBlank()) {
                    throw IllegalArgumentException("Room name must not be blank")
                }
                if (!userEmail.isValidEmail()) {
                    throw IllegalArgumentException("Invalid user email")
                }

                val chatRoom = chatRoomDao.create(
                    roomName = roomName,
                    roomPicUrl = roomPicUrl
                )

                if (chatRoom == null) {
                    throw Exception("Error occurred while creating user.")
                }

                addUserToChatRoom(userEmail, chatRoom.id)

                participants.forEach { email ->
                    val user = getUser(email.toString())

                    if (user == null) {
                        call.respond(HttpStatusCode.NotFound, "User $email does not exist")
                    }

                    addUserToChatRoom(email.toString(), chatRoom.id)
                }

                call.respond(HttpStatusCode.Created, message = chatRoom)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, message = e.stackTraceToString())
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, message = e.message ?: e.stackTraceToString())
            }
        }

        get("/$BASE/getRoom/{chatRoomId}") {
            try {
                val id = call.parameters["chatRoomId"]?.toInt() ?: return@get call.respond(HttpStatusCode.BadRequest)
                val chatRoom = chatRoomDao.getById(id)

                if (chatRoom == null) {
                    call.respond(HttpStatusCode.NotFound, message = "Chat room not found.")
                } else {
                    call.respond(HttpStatusCode.OK, message = chatRoom)
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, message = e.stackTraceToString())
            }
        }

        put("/$BASE/update/{chatRoomId}") {
            try {
                val chatRoomId = call.parameters["chatRoomId"]?.toInt() ?: return@put

                val formParameters = call.receiveParameters()
                val roomName = formParameters["roomName"]
                val roomProfilePic = formParameters["roomPicUrl"]

                val isUpdated = chatRoomDao.updateRoom(
                    roomId = chatRoomId,
                    roomName = roomName,
                    roomProfilePic = roomProfilePic
                )

                if (isUpdated) {
                    call.respond(HttpStatusCode.OK, message = "Chat room updated.")
                } else {
                    call.respond(HttpStatusCode.InternalServerError, message = "Chat room not updated.")
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, message = e.stackTraceToString())
            }
        }
    }
}