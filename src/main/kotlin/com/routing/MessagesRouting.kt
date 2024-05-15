package com.routing

import com.dao.MessagesDao
import com.model.Message
import com.model.PaginatedMessages
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

private const val BASE = "messages"
private const val PAGE_SIZE = 20

fun Application.configureMessageRouting(messagesDao: MessagesDao) {
    routing {

        post("/$BASE/createMessage") {
            try {
                val formParameters = call.receiveParameters()
                val senderId = formParameters.getOrFail("senderId").toInt()
                val senderProfilePicUrl = formParameters["senderProfilePicUrl"]
                val roomId = formParameters.getOrFail<Int>("roomId")
                val messageText = formParameters.getOrFail("messageText")

                val message = messagesDao.create(
                    senderId = senderId,
                    senderProfilePicUrl = senderProfilePicUrl,
                    messageText = messageText,
                    roomId = roomId
                )

                if (message == null) {
                    call.respond(HttpStatusCode.InternalServerError, message = "Message could not be created.")
                } else {
                    call.respond(HttpStatusCode.Created, message)
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, e.stackTraceToString())
            }
        }

        get("/$BASE/getMessages/{roomId}/{page?}") {
            try {
                val roomId = call.parameters["roomId"]?.toInt() ?: return@get
                val page = call.parameters["page"]?.toInt()

                val totalPages = messagesDao.getTotalItems(roomId = roomId, pageSize = PAGE_SIZE)

                val messageList: List<Message> = if ((page ?: 0) > totalPages) {
                    emptyList()
                } else {
                    messagesDao.getById(
                        roomId = roomId,
                        page = page ?: 0,
                        pageSize = PAGE_SIZE
                    )
                }

                call.respond(
                    HttpStatusCode.OK,
                    PaginatedMessages(messageList = messageList, totalPages = totalPages)
                )
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, e.stackTraceToString())
            }
        }
    }
}