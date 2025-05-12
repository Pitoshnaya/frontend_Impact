package pitoshnaya.impactGame.auth

import com.google.gson.Gson
import ktx.async.HttpRequestResult
import pitoshnaya.impactGame.Config
import pitoshnaya.impactGame.network.Address
import pitoshnaya.impactGame.network.JsonRequest
import pitoshnaya.impactGame.network.ServerError
import java.net.ConnectException
import kotlin.collections.get

object AuthServer {
    private const val TOKEN_NAME = "token"

    private var currentUser: User? = null

    private val address: Address = Config.SERVER_ADDRESS

    suspend fun login(login: String, password: String): Result<User> {
        val response: HttpRequestResult

        try {
            response = JsonRequest.post(
                address.resolveURL("/api/login"),
                content = mapOf("username" to login, "password" to password) as Object
            )
        } catch (_: ConnectException) {
            return Result.failure(ServerError.connectionRefused())
        }

        if (response.statusCode != 200) {
            return Result.failure(ServerError.wrongCredentials())
        }

        val payload = Gson().fromJson(response.getContentAsString(), Map::class.java)

        val user = User(login, AuthToken(payload["token"] as String))
        currentUser = user

        Config.save(TOKEN_NAME, user.getToken().value)

        return Result.success(user)
    }

    suspend fun createAccount(login: String, password: String): Result<User> {
        val response: HttpRequestResult

        try {
            response = JsonRequest.post(
                address.resolveURL("/api/register"),
                content = mapOf("username" to login, "password" to password) as Object
            )
        } catch (_: ConnectException) {
            return Result.failure(ServerError.connectionRefused())
        }

        if (response.statusCode != 200) {
            return Result.failure(ServerError(response.contentAsString))
        }

        return login(login, password)
    }

    fun getCurrentUser(): User? {
        return currentUser
    }

    fun tryToAutologin(): Boolean {
        val rawToken = Config.getOrNull<String>(TOKEN_NAME)
        if (rawToken == null || rawToken.isEmpty()) {
            return false
        }

        val token = AuthToken(rawToken)
        if (!token.isValid()) {
            Config.delete(TOKEN_NAME)

            return false
        }

        currentUser = User(token.owner, token)

        return true
    }
}
