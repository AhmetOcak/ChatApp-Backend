package com

import com.dao.impl.*
import com.plugins.*
import com.routing.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val messagesDao = MessagesDaoImpl()

    configureSockets(messagesDao = messagesDao)
    configureSerialization()
    configureDatabases()
    configureUserRouting(userDao = UserDaoImpl())
    configureChatRooms(
        chatRoomDao = ChatRoomsDaoImpl(),
        addUserToChatRoom = UserChatRoomsDaoImpl()::addUserToRoom,
        getUser = UserDaoImpl()::getByEmail
    )
    configureMessageRouting(messagesDao = messagesDao)
    configureUserChatRoomsRouting(
        userChatRoomsDao = UserChatRoomsDaoImpl(),
        getRoom = ChatRoomsDaoImpl()::getById
    )
    configureFriendRouting(friendDao = FriendDaoImpl(), userDao = UserDaoImpl())
}
