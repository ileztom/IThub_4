package com.example.ws

import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import java.util.concurrent.ConcurrentHashMap
import java.util.UUID

data class WsUser(val id: String, val username: String, val session: DefaultWebSocketServerSession)

object ChatManager {
    // roomId -> set of users
    private val rooms = ConcurrentHashMap<String, ConcurrentHashMap<String, WsUser>>()

    fun joinRoom(roomId: String, user: WsUser) {
        val set = rooms.computeIfAbsent(roomId) { ConcurrentHashMap() }
        set[user.id] = user
    }

    fun leaveRoom(roomId: String, userId: String) {
        rooms[roomId]?.remove(userId)
    }

    suspend fun broadcastToRoom(roomId: String, message: String) {
        val users = rooms[roomId]?.values ?: return
        val copy = ArrayList(users)
        for (u in copy) {
            try {
                u.session.send(Frame.Text(message))
            } catch (e: ClosedReceiveChannelException) {
                // ignore
            }
        }
    }
}
