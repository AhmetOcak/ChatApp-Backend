package com.routing

import com.dao.impl.ChatRoomsDaoImpl
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import java.lang.IllegalArgumentException

private const val BASE = "chatRoom"

fun Application.configureChatRooms(chatRoomDao: ChatRoomsDaoImpl) {
    routing {

        post("/$BASE/create") {
            try {
                val formParameters = call.receiveParameters()
                val roomName = formParameters.getOrFail("roomName")
                val roomPicUrl = formParameters["roomPicUrl"]

                if (roomName.isBlank()) {
                    throw IllegalArgumentException("Room name must not be blank")
                }

                val chatRoom = chatRoomDao.create(
                    roomName = roomName,
                    roomPicUrl = roomPicUrl
                )

                if (chatRoom == null) {
                    throw Exception("Error occurred while creating user.")
                }

                call.respond(HttpStatusCode.Created, message = "New chat room created")
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, message = e.stackTraceToString())
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, message = e.message ?: e.stackTraceToString())
            }
        }

        get("/$BASE/getRoom/{id}") {
            try {
                val id = call.parameters["id"]?.toInt() ?: return@get call.respond(HttpStatusCode.BadRequest)
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