package pitoshnaya.impact_game.auth

import com.google.gson.Gson
import ktx.async.HttpRequestResult
import ktx.async.httpRequest
import pitoshnaya.impact_game.network.ServerError
import java.net.ConnectException

data class User(private val name: String, private var token: AuthToken = AuthToken.none()) {
    init {
        require(name != "")
    }

    fun isAuthenticated(): Boolean {
        return token.isValid()
    }
}