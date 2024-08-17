package com.routing

import com.core.isValidEmail
import com.core.setContent
import com.dao.ChatGroupDao
import com.dao.FcmTokenDao
import com.dao.MessagesDao
import com.firebase.sendFcmMessage
import com.model.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.lang.IllegalArgumentException

private const val BASE = "messages"
private const val PAGE_SIZE = 20

fun Application.configureMessageRouting(
    messagesDao: MessagesDao,
    fcmTokenDao: FcmTokenDao,
    chatGroupDao: ChatGroupDao
) {
    routing {

        get("/$BASE/getMessages/{messageBoxId}/{page?}") {
            try {
                val messageBoxId = call.parameters["messageBoxId"]?.toInt() ?: return@get
                val page = call.parameters["page"]?.toInt()

                val totalPages = messagesDao.getTotalItems(
                    messageBoxId = messageBoxId,
                    pageSize = PAGE_SIZE
                )

                val messageList: List<Message> = if ((page ?: 0) > totalPages) {
                    emptyList()
                } else {
                    messagesDao.getById(
                        messageBoxId = messageBoxId,
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

        post("/$BASE/sendMessage") {
            try {
                val receivedMessage = call.receive<Message>()

                if (!receivedMessage.senderEmail.isValidEmail()) {
                    throw IllegalArgumentException("Email is not valid")
                }

                val message = with(receivedMessage) {
                    messagesDao.create(
                        messageBoxId = messageBoxId,
                        senderEmail = senderEmail,
                        messageContent = messageContent,
                        senderImgUrl = senderImgUrl,
                        senderUsername = senderUsername,
                        messageType = messageType
                    )
                }

                if (message == null) {
                    call.respond(HttpStatusCode.InternalServerError, "Message could not be created")
                } else {
                    val participants = chatGroupDao.getGroupParticipants(receivedMessage.messageBoxId)
                    participants.forEach { email ->
                        // Send notification message to every participant
                        if (email != message.senderEmail) {
                            fcmTokenDao.get(email)?.let {
                                sendFcmMessage(
                                    notificationContent = NotificationContent(
                                        title = message.senderUsername,
                                        message = setContent(message)
                                    ),
                                    token = it
                                )
                            }
                        }
                    }

                    call.respond(HttpStatusCode.OK, message)
                }
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, message = e.stackTraceToString())
            } catch (e: java.lang.Exception) {
                call.respond(HttpStatusCode.InternalServerError, message = e.message ?: e.stackTraceToString())
            }
        }

        get("$BASE/getAllMediaMessages/{messageBoxId}") {
            try {
                val messageBoxId = call.parameters["messageBoxId"]?.toInt() ?: return@get
                val messages = messagesDao.getAllMediaTypeMessages(messageBoxId)

                call.respond(HttpStatusCode.OK, messages)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, e.stackTraceToString())
            }
        }
    }
}