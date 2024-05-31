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

        get("/$BASE/getMessages/{userEmail}/{friendEmail}/{page?}") {
            try {
                val userEmail = call.parameters["userEmail"] ?: return@get
                val friendEmail = call.parameters["friendEmail"] ?: return@get
                val page = call.parameters["page"]?.toInt()


                val totalPages = messagesDao.getTotalItems(
                    senderEmail = userEmail,
                    receiverEmail = friendEmail,
                    pageSize = PAGE_SIZE
                )

                val messageList: List<Message> = if ((page ?: 0) > totalPages) {
                    emptyList()
                } else {
                    messagesDao.getById(
                        senderEmail = userEmail,
                        receiverEmail = friendEmail,
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