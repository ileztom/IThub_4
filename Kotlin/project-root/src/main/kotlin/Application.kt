package com.example

import io.ktor.server.application.*
import io.ktor.server.websocket.*
import io.ktor.server.application.*
import io.ktor.server.websocket.*
import plugins.configureAuth
import java.time.Duration

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    // WebSockets
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(30)
        timeout = Duration.ofSeconds(60)
        maxFrameSize = Long.MAX_VALUE
    }

    // JWT Authentication
    configureAuth()

    fun Application.configureWebSockets() {
        install(WebSockets) {
            pingPeriod = Duration.ofSeconds(30)
            timeout = Duration.ofSeconds(60)
            maxFrameSize = Long.MAX_VALUE
            masking = false
        }
    }
}
