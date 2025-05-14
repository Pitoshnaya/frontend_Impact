package pitoshnaya.impactGame.auth

import com.google.gson.Gson
import java.time.Instant
import java.time.LocalDateTime
import java.util.Base64
import java.util.TimeZone

private val decoder: Base64.Decoder = Base64.getUrlDecoder()

class AuthToken(val value: String) {
    val owner: String
    val expiresAt: LocalDateTime

    init {
        var sub = ""
        var expiresAt = LocalDateTime.now()
        if (!value.isEmpty()) {
            val parts: List<String> = value.split('.')
            check(parts.size == 3)

            val payload = Gson().fromJson(
                String(decoder.decode(parts[1])),
                Map::class.java
            )

            sub = payload["sub"] as String
            expiresAt = LocalDateTime.ofInstant(
                Instant.ofEpochSecond((payload["exp"] as Double).toLong()),
                TimeZone.getDefault().toZoneId()
            )
        }

        this.owner = sub
        this.expiresAt = expiresAt
    }

    companion object {
        fun none() : AuthToken = AuthToken("")
    }

    fun isValid(): Boolean {
        // TODO нужна будет проверка на jwt в какой-то момент
        return !isEmpty() && !isExpired()
    }

    fun isEmpty(): Boolean {
        return value.isEmpty()
    }

    fun isExpired(): Boolean {
        return expiresAt.isBefore(LocalDateTime.now())
    }
}
