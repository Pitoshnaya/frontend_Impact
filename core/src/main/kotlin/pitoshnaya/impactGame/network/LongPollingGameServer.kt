package pitoshnaya.impactGame.network

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ktx.async.HttpRequestResult
import pitoshnaya.impactGame.Config
import pitoshnaya.impactGame.auth.AuthServer
import pitoshnaya.impactGame.auth.User
import java.net.ConnectException
import java.time.Duration
import java.time.LocalDateTime

object LongPollingGameServer: NetworkClient {
    private var address: Address = Config.SERVER_ADDRESS

    private var user: User? = null

    private var autoDisconnect: Job? = null

    // Scope best to be kept on module level rather than here
    private val scope = CoroutineScope(Dispatchers.Default)

    private var eventListener: ServerEventListener? = null

    override fun connect() {
        val player = AuthServer.getCurrentUser()
        if (player == null || !player.isAuthenticated()) {
            eventListener?.handle(ServerEvent("disconnected"))
            return
        }

        user = player

        val currentTime = LocalDateTime.now()
        val disconnectAt = user!!.getToken().expiresAt

        autoDisconnect?.cancel()
        autoDisconnect = scope.launch {
            delay(Duration.between(currentTime, disconnectAt).toMillis())
            disconnect()
        }
    }

    override fun disconnect() {
        if (user != null) {
            user = null
            eventListener?.handle(ServerEvent("disconnected"))
        }
        autoDisconnect?.cancel()
        autoDisconnect = null

    }

    override suspend fun send(event: ClientEvent) {
        require(user != null && user!!.isAuthenticated())

        val response: HttpRequestResult

        try {
            response = JsonRequest.GET(
                address.resolveURL("/api/${event.name.trimStart('/')}"),
                headers = mapOf("Authorization" to "Bearer ${user!!.getToken().value}")
            )
        } catch (_: ConnectException) {
            disconnect()
            return
        }

        // If it's not status code 200, then we consider disconnect
        if (!response.statusCode.toString().startsWith("2")) {
            disconnect()

            return
        }

        eventListener?.handle(ServerEvent(event.name, response.getContentAsString()))
    }

    override fun addServerEventListener(listener: ServerEventListener) {
        eventListener = listener
    }

    override fun removeServerEventListener(listener: ServerEventListener) {
        check(eventListener == null || listener === eventListener) { "This implementation does not support multiple listeners" }

        eventListener = null
    }
}
