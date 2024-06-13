package com.routing

import com.dao.MessagesDao
import com.model.Message
import com.model.PaginatedMessages
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private const val BASE = "messages"
private const val PAGE_SIZE = 20

fun Application.configureMessageRouting(messagesDao: MessagesDao) {
    routing {

        get("/$BASE/getMessages/{friendshipId}/{page?}") {
            try {
                val friendshipId = call.parameters["friendshipId"]?.toInt() ?: return@get
                val page = call.parameters["page"]?.toInt()


                val totalPages = messagesDao.getTotalItems(
                    friendshipId = friendshipId,
                    pageSize = PAGE_SIZE
                )

                val messageList: List<Message> = if ((page ?: 0) > totalPages) {
                    emptyList()
                } else {
                    messagesDao.getById(
                        friendshipId = friendshipId,
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