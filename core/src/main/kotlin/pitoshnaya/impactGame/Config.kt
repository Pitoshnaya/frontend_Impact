package pitoshnaya.impactGame

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import io.github.cdimascio.dotenv.dotenv
import ktx.preferences.get
import ktx.preferences.set
import pitoshnaya.network.Address

object Config {
    private val env = dotenv {
        filename = ".env.local"
        ignoreIfMissing = true
    }

    val SERVER_ADDRESS: Address = Address(
        (env.get("SERVER_ADDRESS") ?: "127.0.0.1"),
        env.get("SERVER_PORT")?.toInt() ?: 8080
    )

    val DEBUG_MODE: Boolean = env.get("DEBUG_MODE")?.toBoolean() ?: false

    // По сути будет ссылаться на файл в папке пользователя. Например на unix ~/.prefs/pitoshnaya_impact_game.conf
    val data: Preferences = Gdx.app.getPreferences("pitoshnaya_impact_game.conf")

    inline fun <reified T> getOrNull(name: String): T? {
        return data[name]
    }

    fun save(name: String, value: Any) {
        data[name] = value
        data.flush()
    }

    fun delete(name: String) {
        data.remove(name)
        data.flush()
    }
}
