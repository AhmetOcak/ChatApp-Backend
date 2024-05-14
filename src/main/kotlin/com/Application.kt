package com

import com.dao.impl.ChatRoomsDaoImpl
import com.dao.impl.UserDaoImpl
import com.plugins.*
import com.routing.configureChatRooms
import com.routing.configureUserRouting
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSockets()
    configureSerialization()
    configureDatabases()
    configureUserRouting(userDao = UserDaoImpl())
    configureChatRooms(chatRoomDao = ChatRoomsDaoImpl())
}
