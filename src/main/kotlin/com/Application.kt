package com

import com.dao.impl.UserDaoImpl
import com.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSockets()
    configureSerialization()
    configureDatabases()
    configureUserRouting(userDao = UserDaoImpl())
}
