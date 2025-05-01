package pitoshnaya.impact_game.network

import com.google.gson.Gson
import ktx.async.HttpRequestResult
import ktx.async.httpRequest
import pitoshnaya.impact_game.auth.AuthToken
import pitoshnaya.impact_game.auth.User
import java.net.ConnectException
import java.net.InetSocketAddress

object GameServer {
    private val serializer: Gson = Gson()

    private lateinit var address: String

    // Technically, this must be one-time caller. Though, it can be a circuit switcher to less loaded instance
    fun use(address: InetSocketAddress) {
        this.address = address.hostName + ":" + address.port
    }

    // SRP violation since "login" belongs to loginServer. Just skipping early overengineering
    suspend fun login(login: String, password: String): Result<User> {
        val response: HttpRequestResult

        try {
            response = sendRequest(
                "/api/login",
               "POST",
                mapOf("username" to login, "password" to password) as Object
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
                "/api/register",
                "POST",
                mapOf("username" to login, "password" to password) as Object
            )
        } catch (_: ConnectException) {
            return Result.failure(ServerError.connectionRefused())
        }

        if (response.statusCode != 200) {
            return Result.failure(ServerError(response.contentAsString))
        }

        return login(login, password)
    }

    fun connect(user: User): Result<Unit> {
        if (!user.isAuthenticated()) {
            return Result.failure(ServerError.authenticationRequired())
        }

        return Result.success(Unit);
    }

    private suspend fun sendRequest(path: String, method: String = "GET", content: Object? = null): HttpRequestResult {
        val method = method.uppercase()
        val isValidRequest = !(content !== null && method == "GET")
        check(isValidRequest)

        return httpRequest(
            url = "$address/${path.trimStart('/')}",
            method = method,
            headers = mapOf("Content-Type" to "application/json"),
            content = if (content == null) null else serializer.toJson(content)
        )
    }
}
