package com

import com.dao.impl.*
import com.firebase.FirebaseAdmin
import com.plugins.*
import com.routing.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    FirebaseAdmin.init()

    configureSockets(messagesDao = MessagesDaoImpl(), fcmTokenDao = FcmTokenDaoImpl(), chatGroupDao = ChatGroupDaoImpl())
    configureSerialization()
    configureDatabases()
    configureUserRouting(userDao = UserDaoImpl(), chatGroupDao = ChatGroupDaoImpl())
    configureMessageRouting(
        messagesDao = MessagesDaoImpl(),
        fcmTokenDao = FcmTokenDaoImpl(),
        chatGroupDao = ChatGroupDaoImpl()
    )
    configureFcmTokenRouting(fcmTokenDao = FcmTokenDaoImpl(), userDao = UserDaoImpl())
    configureChatGroupRouting(chatGroupDao = ChatGroupDaoImpl(), userDao = UserDaoImpl())
}
