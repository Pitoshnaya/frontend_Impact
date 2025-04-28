package pitoshnaya.impact_game

import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv

object Config {
    private val env = dotenv {
        filename = ".env.local"
        ignoreIfMissing = true
    }

    val SERVER_ADDRESS: String = env.get("SERVER_ADDRESS") ?: "http://127.0.0.1"
    val SERVER_PORT: Int = env.get("SERVER_PORT")?.toInt() ?: 8080
    val DEBUG_MODE: Boolean = env.get("DEBUG_MODE")?.toBoolean() ?: false
}