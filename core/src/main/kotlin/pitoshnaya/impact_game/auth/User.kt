package pitoshnaya.impact_game.auth

import com.google.gson.Gson
import ktx.async.httpRequest

class User(private val name: String) {
    init {
        require(name != "")
    }

    private var token: AuthToken = AuthToken.none()

    suspend fun login(withPassword: String): Result<Boolean> {
        // TODO
        val response = httpRequest("http://127.0.0.1:8000/login")
        if (response.statusCode != 200) {
            return Result.failure(Error("Incorrect credentials"))
        }

        token = Gson().fromJson(response.getContentAsString(), AuthToken::class.java)

        return Result.success(true)
    }

    fun isAuthenticated(): Boolean {
        return token.isValid()
    }
}