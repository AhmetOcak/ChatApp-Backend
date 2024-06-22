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
    val messagesDao = MessagesDaoImpl()

    FirebaseAdmin.init()

    configureSockets(messagesDao = messagesDao, fcmTokenDao = FcmTokenDaoImpl())
    configureSerialization()
    configureDatabases()
    configureUserRouting(userDao = UserDaoImpl())
    configureChatRooms(
        chatRoomDao = ChatRoomsDaoImpl(),
        addUserToChatRoom = UserChatRoomsDaoImpl()::addUserToRoom,
        getUser = UserDaoImpl()::getByEmail
    )
    configureMessageRouting(messagesDao = messagesDao, fcmTokenDao = FcmTokenDaoImpl())
    configureUserChatRoomsRouting(
        userChatRoomsDao = UserChatRoomsDaoImpl(),
        getRoom = ChatRoomsDaoImpl()::getById
    )
    configureFriendRouting(friendDao = FriendDaoImpl(), userDao = UserDaoImpl())
    configureFcmTokenRouting(fcmTokenDao = FcmTokenDaoImpl(), userDao = UserDaoImpl())
}
