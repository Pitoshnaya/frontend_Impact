package pitoshnaya.impact_game.network

import com.google.gson.Gson
import kotlinx.coroutines.delay
import ktx.async.HttpRequestResult
import ktx.async.httpRequest
import pitoshnaya.impact_game.auth.AuthToken
import pitoshnaya.impact_game.auth.User
import java.net.ConnectException
import java.net.InetSocketAddress

private enum class Method {
    GET, POST
}

private data class Request(
    val method: Method,
    val path: String,
    val headers: Map<String, String> = emptyMap(),
    val content: Object? = null
)

// TODO вынести login/createAccount в логинсервер?
object GameServer {
    private val serializer: Gson = Gson()

    private lateinit var address: String

    private var user: User? = null

    // Technically, this must be one-time caller. Though, it can be a circuit switcher to less loaded instance
    fun use(address: InetSocketAddress) {
        this.address = address.hostName + ":" + address.port
    }

    // SRP violation since "login" belongs to loginServer. Just skipping early overengineering
    suspend fun login(login: String, password: String): Result<User> {
        val response: HttpRequestResult

        try {
            response = sendRequest(
                Request(
                    Method.POST,
                    "/api/login",
                    content = mapOf("username" to login, "password" to password) as Object
                )
            )
        } catch (_: ConnectException) {
            return Result.failure(ServerError.connectionRefused())
        }

        if (response.statusCode != 200) {
            return Result.failure(ServerError.wrongCredentials())
        }

        val token = Gson().fromJson(response.getContentAsString(), AuthToken::class.java)

        return Result.success(User(login, token))
    }

    suspend fun createAccount(login: String, password: String): Result<User> {
        val response: HttpRequestResult

        try {
            response = sendRequest(
                Request(
                    Method.POST,
                    "/api/register",
                    content = mapOf("username" to login, "password" to password) as Object
                )
            )
        } catch (_: ConnectException) {
            return Result.failure(ServerError.connectionRefused())
        }

        if (response.statusCode != 200) {
            return Result.failure(ServerError(response.contentAsString))
        }

        return login(login, password)
    }

    fun connect(user: User) {
        require(user.isAuthenticated())

        this.user = user
    }

    suspend fun consume(resourcePath: String): Result<String> {
        require(user != null && user!!.isAuthenticated())

        // TODO убрать. Пока просто имитация длительности прогрузки
        delay(1000)

        val response: HttpRequestResult

        try {
            response = sendRequest(
                Request(
                    Method.GET,
                    "/api/${resourcePath.trimStart('/')}",
                    headers = mapOf("Authorization" to "Bearer ${user!!.getToken().value}")
                )
            )
        } catch (_: ConnectException) {
            return Result.failure(ServerError.connectionRefused())
        }

        if (response.statusCode != 200) {
            return Result.failure(ServerError(response.contentAsString))
        }

        return Result.success(response.contentAsString)
    }

    private suspend fun sendRequest(request: Request): HttpRequestResult {
        val isValidRequest = !(request.content !== null && request.method == Method.GET)
        check(isValidRequest)

        return httpRequest(
            url = "$address/${request.path.trimStart('/')}",
            method = request.method.toString(),
            headers = mapOf("Content-Type" to "application/json") + request.headers,
            content = if (request.content == null) null else serializer.toJson(request.content)
        )
    }
}
