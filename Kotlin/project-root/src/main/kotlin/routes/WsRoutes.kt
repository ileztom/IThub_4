package routes

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Duration

data class ChatUser(val username: String, val role: String, val session: DefaultWebSocketServerSession)

fun Route.wsRoutes() {

    val clients = mutableListOf<ChatUser>()
    val mutex = Mutex()

    webSocket("/chat") {
        val principal = call.principal<JWTPrincipal>()
        if (principal == null) {
            close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Unauthorized"))
            return@webSocket
        }

        val username = principal.payload.getClaim("username").asString()
        val role = principal.payload.getClaim("role").asString()

        val user = ChatUser(username, role, this)
        mutex.withLock { clients.add(user) }

        send("✅ Welcome, $username! Your role: $role")

        try {
            incoming.consumeEach { frame ->
                when (frame) {
                    is Frame.Text -> {
                        val message = frame.readText()
                        if (role == "admin" && message.startsWith("/announce ")) {
                            val announcement = "📢 ADMIN: ${message.removePrefix("/announce ")}"
                            mutex.withLock {
                                clients.forEach { it.session.send(announcement) }
                            }
                        } else {
                            val fullMessage = "💬 [$username]: $message"
                            mutex.withLock {
                                clients.filter { it.session != this }.forEach {
                                    it.session.send(fullMessage)
                                }
                            }
                        }
                    }
                    is Frame.Binary -> {}
                    is Frame.Ping -> send(Frame.Pong(frame.buffer))
                    is Frame.Pong -> {}
                    is Frame.Close -> {
                        println("❌ $username disconnected")
                        mutex.withLock { clients.remove(user) }
                    }
                }
            }
        } catch (e: Exception) {
            println("⚠️ WebSocket error: ${e.localizedMessage}")
        } finally {
            mutex.withLock { clients.remove(user) }
            close(CloseReason(CloseReason.Codes.NORMAL, "Session closed"))
        }
    }

}
