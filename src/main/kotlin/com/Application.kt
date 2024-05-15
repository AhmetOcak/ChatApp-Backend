package com

import com.dao.impl.ChatRoomsDaoImpl
import com.dao.impl.MessagesDaoImpl
import com.dao.impl.UserChatRoomsDaoImpl
import com.dao.impl.UserDaoImpl
import com.plugins.*
import com.routing.configureChatRooms
import com.routing.configureMessageRouting
import com.routing.configureUserChatRoomsRouting
import com.routing.configureUserRouting
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
    configureChatRooms(chatRoomDao = ChatRoomsDaoImpl())
    configureMessageRouting(messagesDao = messagesDao)
    configureUserChatRoomsRouting(
        userChatRoomsDao = UserChatRoomsDaoImpl(),
        getRoom = ChatRoomsDaoImpl()::getById
    )
}
