package pitoshnaya.impact_game.network

import com.google.gson.Gson
import ktx.async.HttpRequestResult
import ktx.async.httpRequest
import pitoshnaya.impact_game.auth.AuthToken
import pitoshnaya.impact_game.auth.User
import java.net.ConnectException
import java.net.InetSocketAddress

object GameServer {
    private lateinit var address: String

    // Technically, this must be one-time caller. Though, it can be a circuit switcher to less loaded instance
    fun use(address: InetSocketAddress) {
        this.address = address.hostName + ":" + address.port
    }

    // SRP violation since "login" belongs to loginServer. Just skipping early overengineering
    suspend fun login(login: String, password: String): Result<User> {
        val response: HttpRequestResult

        try {
            response = httpRequest(
                url = "$address/api/login",
                method = "POST",
                headers = mapOf("Content-Type" to "application/json"),
                content = "{\"username\": \"$login\", \"password\": \"$password\"}"
            )
        } catch (e: ConnectException) {
            return Result.failure(ServerError.connectionRefused())
        }

        if (response.statusCode != 200) {
            return Result.failure(Error("Incorrect credentials"))
        }

        val token = Gson().fromJson(response.getContentAsString(), AuthToken::class.java)

        return Result.success(User(login, token))
    }

    fun connect(user: User): Result<Unit> {
        if (!user.isAuthenticated()) {
            return Result.failure(ServerError.authenticationRequired())
        }

        return Result.success(Unit);
    }
}