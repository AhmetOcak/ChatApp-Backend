package com.routing

import com.core.isValidEmail
import com.dao.FcmTokenDao
import com.dao.MessagesDao
import com.firebase.sendFcmMessage
import com.model.Message
import com.model.NotificationContent
import com.model.PaginatedMessages
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.lang.IllegalArgumentException

private const val BASE = "messages"
private const val PAGE_SIZE = 20

fun Application.configureMessageRouting(messagesDao: MessagesDao, fcmTokenDao: FcmTokenDao) {
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

        post("/$BASE/sendMessage") {
            try {
                val receivedMessage = call.receive<Message>()

                if (!receivedMessage.senderEmail.isValidEmail() || !receivedMessage.receiverEmail.isValidEmail()) {
                    throw IllegalArgumentException("Email is not valid")
                }

                val message = messagesDao.create(
                    friendshipId = receivedMessage.friendshipId,
                    senderEmail = receivedMessage.senderEmail,
                    receiverEmail = receivedMessage.receiverEmail,
                    messageContent = receivedMessage.messageContent,
                    senderImgUrl = receivedMessage.senderImgUrl,
                    senderUsername = receivedMessage.senderUsername,
                    messageType = receivedMessage.messageType
                )

                if (message == null) {
                    call.respond(HttpStatusCode.InternalServerError, "Message could not be created")
                } else {
                    call.respond(HttpStatusCode.OK, message)

                    // Send notification message to receiver user
                    fcmTokenDao.get(message.receiverEmail)?.let {
                        sendFcmMessage(
                            notificationContent = NotificationContent(
                                title = message.senderUsername,
                                message = message.messageContent
                            ),
                            token = it
                        )
                    }
                }
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, message = e.stackTraceToString())
            } catch (e: java.lang.Exception) {
                call.respond(HttpStatusCode.InternalServerError, message = e.message ?: e.stackTraceToString())
            }
        }
    }
}