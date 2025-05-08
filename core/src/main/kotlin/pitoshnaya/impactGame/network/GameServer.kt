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

object GameServer {
    private var address: Address = Config.SERVER_ADDRESS

    private var user: User? = null

    private var autoDisconnect: Job? = null

    // Скоп лучше держать где-то на уровне компонента, а не здесь
    private val scope = CoroutineScope(Dispatchers.Default)

    fun connect(): Boolean {
        val player = AuthServer.getCurrentUser()
        if (player == null || !player.isAuthenticated()) {
            return false;
        }

        disconnect()

        user = player

        val currentTime = LocalDateTime.now()
        val disconnectAt = user!!.getToken().expiresAt

        autoDisconnect = scope.launch {
            delay(Duration.between(currentTime, disconnectAt).toMillis())
            disconnect()
        }

        return true
    }

    fun disconnect() {
        user = null
        autoDisconnect?.cancel()
        autoDisconnect = null
    }

    suspend fun consume(resourcePath: String): Result<String> {
        require(user != null && user!!.isAuthenticated())

        // TODO убрать. Пока просто имитация длительности прогрузки
        delay(1000)

        val response: HttpRequestResult

        try {
            response = JsonRequest.GET(
                address.resolveURL("/api/${resourcePath.trimStart('/')}"),
                headers = mapOf("Authorization" to "Bearer ${user!!.getToken().value}")
            )
        } catch (_: ConnectException) {
            return Result.failure(ServerError.connectionRefused())
        }

        if (response.statusCode != 200) {
            return Result.failure(ServerError(response.contentAsString))
        }

        return Result.success(response.contentAsString)
    }
}
